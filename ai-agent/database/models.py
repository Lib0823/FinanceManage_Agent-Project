"""SQLAlchemy models for AI Agent database tables."""
from sqlalchemy import Column, String, Date, BigInteger, Numeric, Boolean, create_engine
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker

from config import settings

Base = declarative_base()


class StockFilterScore(Base):
    """Stage 1 output: Stock filtering scores and selection results."""

    __tablename__ = 'stock_filter_score'

    stock_code = Column(String(10), primary_key=True)
    trade_date = Column(Date, primary_key=True)
    foreign_net_buy = Column(BigInteger, nullable=False)
    institution_net_buy = Column(BigInteger, nullable=False)
    volume_ratio = Column(Numeric(10, 4), nullable=False)
    price_volatility = Column(Numeric(10, 4), nullable=False)
    final_score = Column(Numeric(10, 4), nullable=False)
    is_selected = Column(Boolean, nullable=False, default=False)

    def __repr__(self):
        return f"<StockFilterScore({self.stock_code}, {self.trade_date}, score={self.final_score})>"


# Database engine and session factory
engine = create_engine(settings.database_url, echo=False)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)


def get_db():
    """Get database session (dependency injection pattern)."""
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()
