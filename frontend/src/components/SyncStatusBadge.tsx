import { Badge } from 'antd';
import type { SyncStatus } from '../types/workspace';

const statusMap: Record<SyncStatus, 'success' | 'warning' | 'error'> = {
  ACTIVE: 'success',
  PENDING: 'warning',
  INACTIVE: 'error',
};

export default function SyncStatusBadge({ status }: { status?: SyncStatus }) {
  if (!status) return null;
  return <Badge status={statusMap[status]} text={status} />;
}
