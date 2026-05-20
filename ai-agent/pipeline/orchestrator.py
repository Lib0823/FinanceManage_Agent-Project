"""Pipeline orchestrator for Stage 1: Stock Filtering."""
import logging
import asyncio
from datetime import date
from typing import Optional, Dict, List

from collectors import KISClient
from analysis import StockFilter
from database import DatabaseRepository
from config.constants import KOSPI_100

logger = logging.getLogger(__name__)


class PipelineOrchestrator:
    """
    Orchestrates Stage 1 pipeline execution.

    Workflow:
    1. Fetch KOSPI 100 stock data from KIS API
    2. Calculate weighted scores using StandardScaler
    3. Select top 30 stocks (+ holdings)
    4. Save results to database
    """

    def __init__(self):
        self.kis_client = KISClient()
        self.stock_filter = StockFilter()
        self.db_repo = DatabaseRepository()

        logger.info("PipelineOrchestrator initialized")

    async def run_stage1_filtering(
        self,
        trade_date: Optional[date] = None,
        holdings: Optional[List[str]] = None
    ) -> Dict:
        """
        Execute Stage 1: Stock filtering pipeline.

        Args:
            trade_date: Trade date (defaults to today)
            holdings: Optional list of holdings to always include

        Returns:
            Dict with execution results:
                - success: bool
                - trade_date: date
                - total_stocks: int
                - selected_stocks: int
                - selected_codes: List[str]
                - error: Optional error message
        """
        if trade_date is None:
            trade_date = date.today()

        logger.info(f"Starting Stage 1 filtering for {trade_date}")

        try:
            # Step 1: Fetch KOSPI 100 data from KIS API
            logger.info(f"Fetching data for {len(KOSPI_100)} KOSPI 100 stocks")
            stock_data_df = await self.kis_client.fetch_stock_data_parallel(KOSPI_100)

            if stock_data_df.empty:
                error_msg = "Failed to fetch stock data from KIS API"
                logger.error(error_msg)
                return {
                    'success': False,
                    'trade_date': trade_date,
                    'error': error_msg
                }

            logger.info(f"Fetched data for {len(stock_data_df)} stocks")

            # Step 2: Calculate scores and filter top 30
            logger.info("Calculating scores and filtering top 30 stocks")
            filtered_df = self.stock_filter.process(stock_data_df, holdings=holdings)

            # Step 3: Save results to database
            logger.info("Saving results to database")
            save_success = self.db_repo.save_filter_scores(filtered_df, trade_date)

            if not save_success:
                error_msg = "Failed to save results to database"
                logger.error(error_msg)
                return {
                    'success': False,
                    'trade_date': trade_date,
                    'error': error_msg
                }

            # Extract selected stocks
            selected_df = filtered_df[filtered_df['is_selected'] == True]
            selected_codes = selected_df['stock_code'].tolist()

            logger.info(f"Stage 1 filtering completed successfully: {len(selected_codes)} stocks selected")

            return {
                'success': True,
                'trade_date': trade_date,
                'total_stocks': len(filtered_df),
                'selected_stocks': len(selected_codes),
                'selected_codes': selected_codes,
                'score_stats': {
                    'min': float(filtered_df['final_score'].min()),
                    'max': float(filtered_df['final_score'].max()),
                    'mean': float(filtered_df['final_score'].mean())
                }
            }

        except Exception as e:
            error_msg = f"Pipeline execution failed: {str(e)}"
            logger.exception(error_msg)
            return {
                'success': False,
                'trade_date': trade_date,
                'error': error_msg
            }

    def run_stage1_sync(
        self,
        trade_date: Optional[date] = None,
        holdings: Optional[List[str]] = None
    ) -> Dict:
        """
        Synchronous wrapper for run_stage1_filtering (for scheduler).

        Args:
            trade_date: Trade date (defaults to today)
            holdings: Optional list of holdings to always include

        Returns:
            Dict with execution results
        """
        return asyncio.run(self.run_stage1_filtering(trade_date, holdings))
