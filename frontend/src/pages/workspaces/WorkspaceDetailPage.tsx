import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Descriptions, Tabs, Button, Spin } from 'antd';
import { ArrowLeftOutlined } from '@ant-design/icons';
import type { Workspace } from '../../types/workspace';
import * as workspacesApi from '../../api/workspaces';
import SyncStatusBadge from '../../components/SyncStatusBadge';
import SnapshotsTab from '../snapshots/SnapshotsTab';
import PricesTab from '../prices/PricesTab';

export default function WorkspaceDetailPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [workspace, setWorkspace] = useState<Workspace | null>(null);

  useEffect(() => {
    if (id) workspacesApi.getById(Number(id)).then(setWorkspace);
  }, [id]);

  if (!workspace) return <Spin />;

  const workspaceId = Number(id);

  return (
    <>
      <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/workspaces')} style={{ marginBottom: 16 }}>
        Back
      </Button>
      <Descriptions bordered column={2} style={{ marginBottom: 24 }}>
        <Descriptions.Item label="Host">{workspace.host}</Descriptions.Item>
        <Descriptions.Item label="Email">{workspace.email}</Descriptions.Item>
        <Descriptions.Item label="Currency">{workspace.currency}</Descriptions.Item>
        <Descriptions.Item label="Pricing Strategy">{workspace.pricingStrategy}</Descriptions.Item>
        <Descriptions.Item label="Sync Status">
          <SyncStatusBadge status={workspace.syncStatus} />
        </Descriptions.Item>
        <Descriptions.Item label="Last Sync">{workspace.lastSyncTime ?? '—'}</Descriptions.Item>
      </Descriptions>
      <Tabs
        items={[
          { key: 'snapshots', label: 'Snapshots', children: <SnapshotsTab workspaceId={workspaceId} /> },
          { key: 'prices', label: 'Prices', children: <PricesTab workspaceId={workspaceId} /> },
        ]}
      />
    </>
  );
}
