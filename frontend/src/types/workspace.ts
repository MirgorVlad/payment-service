export type Currency = 'UAH' | 'USD' | 'EUR';
export type PricingStrategyType = 'MAX' | 'AVG' | 'LATEST';
export type SyncStatus = 'PENDING' | 'ACTIVE' | 'INACTIVE';

export interface Workspace {
  id?: number;
  email: string;
  host: string;
  password: string;
  currency: Currency;
  pricingStrategy: PricingStrategyType;
  lastSyncTime?: string;
  syncStatus?: SyncStatus;
}
