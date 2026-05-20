"""KIS API client with OAuth authentication and rate limiting."""
import asyncio
import logging
from datetime import datetime, timedelta
from typing import Optional, Dict, List
import aiohttp
import pandas as pd

from config import settings
from config.constants import KIS_MAX_REQUESTS_PER_SECOND, KIS_REQUEST_DELAY

logger = logging.getLogger(__name__)


class KISClient:
    """
    KIS Open API client with async support and rate limiting.

    Features:
    - OAuth token caching (24-hour TTL)
    - TR_ID auto-conversion (VIRTUAL/REAL mode)
    - Rate limiting (5 requests/second)
    - Comprehensive error handling
    """

    def __init__(self):
        self.app_key = settings.kis_app_key
        self.app_secret = settings.kis_app_secret
        self.mode = settings.kis_mode
        self.base_url = settings.kis_base_url

        self.access_token: Optional[str] = None
        self.token_expires_at: Optional[datetime] = None

        # Rate limiting semaphore
        self.semaphore = asyncio.Semaphore(KIS_MAX_REQUESTS_PER_SECOND)

        logger.info(f"KISClient initialized in {self.mode} mode")

    async def get_access_token(self) -> str:
        """
        Get OAuth access token (cached for 24 hours).

        Returns:
            str: Access token for API authorization
        """
        # Return cached token if still valid
        if self.access_token and self.token_expires_at and datetime.now() < self.token_expires_at:
            logger.debug("Using cached access token")
            return self.access_token

        logger.info("Fetching new access token from KIS API")
        url = f'{self.base_url}/oauth2/tokenP'
        data = {
            'grant_type': 'client_credentials',
            'appkey': self.app_key,
            'appsecret': self.app_secret
        }

        try:
            async with aiohttp.ClientSession() as session:
                async with session.post(url, json=data) as response:
                    response.raise_for_status()
                    result = await response.json()

                    self.access_token = result['access_token']
                    # KIS tokens are valid for 24 hours
                    self.token_expires_at = datetime.now() + timedelta(hours=24)

                    logger.info("Access token acquired successfully")
                    return self.access_token

        except aiohttp.ClientError as e:
            logger.error(f"Failed to get access token: {e}")
            raise RuntimeError(f"KIS OAuth failed: {e}")

    def convert_tr_id(self, base_tr_id: str) -> str:
        """
        Convert TR_ID based on mode (VIRTUAL/REAL).

        VIRTUAL: VTTC* (모의투자)
        REAL: TTTC* (실전투자)

        Args:
            base_tr_id: Base TR_ID (e.g., 'VTTC8434R')

        Returns:
            str: Converted TR_ID based on mode
        """
        if base_tr_id is None or len(base_tr_id) < 4:
            return base_tr_id

        suffix = base_tr_id[4:]  # Extract suffix (e.g., '8434R')

        if self.mode == 'REAL' and base_tr_id.startswith('VTTC'):
            converted = f'TTTC{suffix}'
            logger.debug(f"TR_ID converted: {base_tr_id} → {converted}")
            return converted
        elif self.mode == 'VIRTUAL' and base_tr_id.startswith('TTTC'):
            converted = f'VTTC{suffix}'
            logger.debug(f"TR_ID converted: {base_tr_id} → {converted}")
            return converted

        return base_tr_id

    async def request(
        self,
        method: str,
        endpoint: str,
        tr_id: str,
        params: Optional[Dict] = None,
        json_data: Optional[Dict] = None
    ) -> Dict:
        """
        Make KIS API request with rate limiting and authentication.

        Args:
            method: HTTP method ('GET' or 'POST')
            endpoint: API endpoint path
            tr_id: Transaction ID (will be auto-converted)
            params: Query parameters for GET requests
            json_data: JSON body for POST requests

        Returns:
            Dict: API response JSON
        """
        async with self.semaphore:
            token = await self.get_access_token()
            tr_id_converted = self.convert_tr_id(tr_id)

            headers = {
                'authorization': f'Bearer {token}',
                'appkey': self.app_key,
                'appsecret': self.app_secret,
                'tr_id': tr_id_converted,
                'custtype': 'P',  # Personal account
                'content-type': 'application/json'
            }

            url = f'{self.base_url}{endpoint}'

            try:
                async with aiohttp.ClientSession() as session:
                    if method == 'GET':
                        async with session.get(url, headers=headers, params=params) as response:
                            response.raise_for_status()
                            result = await response.json()
                    elif method == 'POST':
                        async with session.post(url, headers=headers, json=json_data) as response:
                            response.raise_for_status()
                            result = await response.json()
                    else:
                        raise ValueError(f"Unsupported HTTP method: {method}")

                    # Check KIS API response code
                    if result.get('rt_cd') != '0':
                        error_msg = result.get('msg1', 'Unknown error')
                        logger.error(f"KIS API error: {error_msg}")
                        raise RuntimeError(f"KIS API error: {error_msg}")

                    # Rate limiting delay
                    await asyncio.sleep(KIS_REQUEST_DELAY)

                    return result

            except aiohttp.ClientError as e:
                logger.error(f"KIS API request failed: {method} {endpoint} - {e}")
                raise RuntimeError(f"KIS API request failed: {e}")

    async def get_supply_demand(self, stock_code: str) -> Dict[str, int]:
        """
        Get foreign and institutional net buy amounts.

        API: FHKST01010900 (외국인·기관 순매수)

        Args:
            stock_code: 6-digit stock code

        Returns:
            Dict with keys:
                - foreign_net_buy: 외국인 순매수 금액 (원)
                - institutional_net_buy: 기관 순매수 금액 (원)
        """
        endpoint = '/uapi/domestic-stock/v1/quotations/inquire-investor'
        tr_id = 'FHKST01010900'

        params = {
            'FID_COND_MRKT_DIV_CODE': 'J',
            'FID_INPUT_ISCD': stock_code
        }

        result = await self.request('GET', endpoint, tr_id, params=params)
        output = result.get('output', {})

        foreign_net_buy = int(output.get('stck_frgn_ntby_amt', 0))
        institutional_net_buy = int(output.get('stck_orgn_ntby_amt', 0))

        logger.debug(f"Supply demand for {stock_code}: foreign={foreign_net_buy}, institutional={institutional_net_buy}")

        return {
            'foreign_net_buy': foreign_net_buy,
            'institutional_net_buy': institutional_net_buy
        }

    async def get_daily_ohlcv(self, stock_code: str, days: int = 30) -> pd.DataFrame:
        """
        Get daily OHLCV data for volume and volatility calculation.

        API: FHKST01010400 (일봉 조회)

        Args:
            stock_code: 6-digit stock code
            days: Number of trading days to fetch (default: 30 for 20-day MA)

        Returns:
            DataFrame with columns:
                - trade_date: 거래일 (YYYYMMDD)
                - open: 시가
                - high: 고가
                - low: 저가
                - close: 종가
                - volume: 거래량
        """
        endpoint = '/uapi/domestic-stock/v1/quotations/inquire-daily-price'
        tr_id = 'FHKST01010400'

        params = {
            'FID_COND_MRKT_DIV_CODE': 'J',
            'FID_INPUT_ISCD': stock_code,
            'FID_PERIOD_DIV_CODE': 'D',  # Daily
            'FID_ORG_ADJ_PRC': '0'  # Not adjusted for rights
        }

        result = await self.request('GET', endpoint, tr_id, params=params)
        output_list = result.get('output', [])

        if not output_list:
            logger.warning(f"No daily data for {stock_code}")
            return pd.DataFrame()

        # Parse daily data (KIS returns newest first)
        data = []
        for item in output_list[:days]:
            data.append({
                'trade_date': item['stck_bsop_date'],
                'open': int(item['stck_oprc']),
                'high': int(item['stck_hgpr']),
                'low': int(item['stck_lwpr']),
                'close': int(item['stck_clpr']),
                'volume': int(item['acml_vol'])
            })

        df = pd.DataFrame(data)
        df = df.sort_values('trade_date').reset_index(drop=True)  # Oldest first

        logger.debug(f"Fetched {len(df)} days of OHLCV for {stock_code}")

        return df

    async def fetch_stock_data_parallel(self, stock_codes: List[str]) -> pd.DataFrame:
        """
        Fetch supply/demand and OHLCV data for multiple stocks in parallel.

        Args:
            stock_codes: List of 6-digit stock codes

        Returns:
            DataFrame with columns:
                - stock_code
                - foreign_net_buy
                - institutional_net_buy
                - volume_ratio (전날 거래량 / 20일 평균 거래량)
                - price_volatility ((고가-저가) / 저가)
        """
        logger.info(f"Fetching data for {len(stock_codes)} stocks in parallel")

        async def fetch_single_stock(stock_code: str) -> Optional[Dict]:
            try:
                # Fetch supply/demand and OHLCV concurrently
                supply_demand_task = self.get_supply_demand(stock_code)
                ohlcv_task = self.get_daily_ohlcv(stock_code, days=21)  # 20-day MA + 1 latest

                supply_demand, ohlcv_df = await asyncio.gather(supply_demand_task, ohlcv_task)

                if ohlcv_df.empty or len(ohlcv_df) < 2:
                    logger.warning(f"Insufficient data for {stock_code}")
                    return None

                # Calculate volume ratio (전날 거래량 / 20일 평균 거래량)
                if len(ohlcv_df) >= 21:
                    volume_ma_20 = ohlcv_df['volume'].iloc[:-1].tail(20).mean()
                    latest_volume = ohlcv_df['volume'].iloc[-1]
                    volume_ratio = latest_volume / volume_ma_20 if volume_ma_20 > 0 else 1.0
                else:
                    # Fallback if less than 21 days
                    volume_ratio = 1.0

                # Calculate price volatility ((고가-저가) / 저가)
                latest_day = ohlcv_df.iloc[-1]
                price_volatility = (latest_day['high'] - latest_day['low']) / latest_day['low'] if latest_day['low'] > 0 else 0.0

                return {
                    'stock_code': stock_code,
                    'foreign_net_buy': supply_demand['foreign_net_buy'],
                    'institutional_net_buy': supply_demand['institutional_net_buy'],
                    'volume_ratio': volume_ratio,
                    'price_volatility': price_volatility
                }

            except Exception as e:
                logger.error(f"Error fetching data for {stock_code}: {e}")
                return None

        # Execute all requests in parallel
        tasks = [fetch_single_stock(code) for code in stock_codes]
        results = await asyncio.gather(*tasks)

        # Filter out None results
        valid_results = [r for r in results if r is not None]

        if not valid_results:
            logger.error("No valid stock data fetched")
            return pd.DataFrame()

        df = pd.DataFrame(valid_results)
        logger.info(f"Successfully fetched data for {len(df)}/{len(stock_codes)} stocks")

        return df
