import type { Currency } from './workspace';

export interface BillingRecord {
  id?: number;
  workspaceId: number;
  deviceCount: number;
  assetCount: number;
  customerCount: number;
  devicePrice: number;
  assetPrice: number;
  customerPrice: number;
  currency: Currency;
  startBillingPeriod: string;
  endBillingPeriod: string;
}

export interface TimeInterval {
  startTime: string;
  endTime: string;
}
