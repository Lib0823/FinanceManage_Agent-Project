"""Database repository for CRUD operations."""
import logging
from datetime import date
from typing import List, Optional
import pandas as pd
from sqlalchemy.orm import Session
from sqlalchemy.exc import SQLAlchemyError

from .models import StockFilterScore, SessionLocal

logger = logging.getLogger(__name__)


class DatabaseRepository:
    """Repository pattern for database operations."""

    def __init__(self):
        self.session_factory = SessionLocal

    def save_filter_scores(self, df: pd.DataFrame, trade_date: date) -> bool:
        """
        Save stock filter scores to database.

        Args:
            df: DataFrame with columns matching StockFilterScore model
            trade_date: Trade date for these scores

        Returns:
            bool: True if successful, False otherwise
        """
        session = self.session_factory()
        try:
            logger.info(f"Saving {len(df)} filter scores for {trade_date}")

            # Delete existing records for this date (if any)
            session.query(StockFilterScore).filter(
                StockFilterScore.trade_date == trade_date
            ).delete()

            # Convert DataFrame to model objects
            records = []
            for _, row in df.iterrows():
                record = StockFilterScore(
                    stock_code=row['stock_code'],
                    trade_date=trade_date,
                    foreign_net_buy=int(row['foreign_net_buy']),
                    institution_net_buy=int(row['institutional_net_buy']),
                    volume_ratio=float(row['volume_ratio']),
                    price_volatility=float(row['price_volatility']),
                    final_score=float(row['final_score']),
                    is_selected=bool(row['is_selected'])
                )
                records.append(record)

            # Bulk insert
            session.bulk_save_objects(records)
            session.commit()

            logger.info(f"Successfully saved {len(records)} filter scores")
            return True

        except SQLAlchemyError as e:
            logger.error(f"Database error while saving filter scores: {e}")
            session.rollback()
            return False

        finally:
            session.close()

    def get_filter_scores(self, trade_date: date) -> Optional[pd.DataFrame]:
        """
        Retrieve filter scores for a specific date.

        Args:
            trade_date: Trade date to retrieve

        Returns:
            DataFrame or None if no data found
        """
        session = self.session_factory()
        try:
            records = session.query(StockFilterScore).filter(
                StockFilterScore.trade_date == trade_date
            ).all()

            if not records:
                logger.warning(f"No filter scores found for {trade_date}")
                return None

            # Convert to DataFrame
            data = [{
                'stock_code': r.stock_code,
                'trade_date': r.trade_date,
                'foreign_net_buy': r.foreign_net_buy,
                'institutional_net_buy': r.institution_net_buy,
                'volume_ratio': float(r.volume_ratio),
                'price_volatility': float(r.price_volatility),
                'final_score': float(r.final_score),
                'is_selected': r.is_selected
            } for r in records]

            df = pd.DataFrame(data)
            logger.info(f"Retrieved {len(df)} filter scores for {trade_date}")

            return df

        except SQLAlchemyError as e:
            logger.error(f"Database error while retrieving filter scores: {e}")
            return None

        finally:
            session.close()

    def get_selected_stocks(self, trade_date: date) -> List[str]:
        """
        Get list of selected stock codes for a specific date.

        Args:
            trade_date: Trade date

        Returns:
            List of stock codes (empty list if none found)
        """
        session = self.session_factory()
        try:
            records = session.query(StockFilterScore.stock_code).filter(
                StockFilterScore.trade_date == trade_date,
                StockFilterScore.is_selected == True
            ).all()

            stock_codes = [r.stock_code for r in records]
            logger.info(f"Retrieved {len(stock_codes)} selected stocks for {trade_date}")

            return stock_codes

        except SQLAlchemyError as e:
            logger.error(f"Database error while retrieving selected stocks: {e}")
            return []

        finally:
            session.close()

    def get_latest_filter_date(self) -> Optional[date]:
        """
        Get the most recent trade date with filter scores.

        Returns:
            date or None if no data exists
        """
        session = self.session_factory()
        try:
            result = session.query(StockFilterScore.trade_date).order_by(
                StockFilterScore.trade_date.desc()
            ).first()

            if result:
                latest_date = result[0]
                logger.info(f"Latest filter date: {latest_date}")
                return latest_date
            else:
                logger.warning("No filter scores found in database")
                return None

        except SQLAlchemyError as e:
            logger.error(f"Database error while retrieving latest date: {e}")
            return None

        finally:
            session.close()
