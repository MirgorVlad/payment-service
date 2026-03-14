import { useEffect, useState } from 'react';
import { Button, Table, Space, Typography, message } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import type { BillingRecord } from '../../types/billing';
import type { TimeInterval } from '../../types/billing';
import * as billingApi from '../../api/billing';
import { useAuth } from '../../context/AuthContext';
import ConfirmDeleteButton from '../../components/ConfirmDeleteButton';
import GenerateBillingModal from './GenerateBillingModal';

export default function BillingPage() {
  const [records, setRecords] = useState<BillingRecord[]>([]);
  const [loading, setLoading] = useState(false);
  const [modalOpen, setModalOpen] = useState(false);
  const [messageApi, contextHolder] = message.useMessage();
  const { role } = useAuth();
  const isAdmin = role === 'ADMIN';

  const load = async () => {
    setLoading(true);
    try {
      setRecords(await billingApi.getAll());
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { load(); }, []);

  const handleGenerate = async (workspaceId: number, interval: TimeInterval) => {
    await billingApi.generate(workspaceId, interval);
    messageApi.success('Billing generated');
    setModalOpen(false);
    load();
  };

  const handleDelete = async (id: number) => {
    await billingApi.remove(id);
    messageApi.success('Deleted');
    load();
  };

  const columns = [
    { title: 'Workspace ID', dataIndex: 'workspaceId', key: 'workspaceId' },
    { title: 'Currency', dataIndex: 'currency', key: 'currency' },
    { title: 'Devices', dataIndex: 'deviceCount', key: 'deviceCount' },
    { title: 'Assets', dataIndex: 'assetCount', key: 'assetCount' },
    { title: 'Customers', dataIndex: 'customerCount', key: 'customerCount' },
    { title: 'Device Price', dataIndex: 'devicePrice', key: 'devicePrice' },
    { title: 'Asset Price', dataIndex: 'assetPrice', key: 'assetPrice' },
    { title: 'Customer Price', dataIndex: 'customerPrice', key: 'customerPrice' },
    { title: 'Start', dataIndex: 'startBillingPeriod', key: 'startBillingPeriod' },
    { title: 'End', dataIndex: 'endBillingPeriod', key: 'endBillingPeriod' },
    {
      title: 'Actions',
      key: 'actions',
      render: (_: unknown, r: BillingRecord) => (
        <ConfirmDeleteButton onConfirm={() => r.id && handleDelete(r.id)} />
      ),
    },
  ];

  return (
    <>
      {contextHolder}
      <Space style={{ marginBottom: 16 }}>
        <Typography.Title level={4} style={{ margin: 0 }}>Billing Records</Typography.Title>
        {isAdmin && (
          <Button type="primary" icon={<PlusOutlined />} onClick={() => setModalOpen(true)}>
            Generate
          </Button>
        )}
      </Space>
      <Table rowKey="id" loading={loading} dataSource={records} columns={columns} pagination={{ pageSize: 10 }} scroll={{ x: true }} />
      <GenerateBillingModal open={modalOpen} onOk={handleGenerate} onCancel={() => setModalOpen(false)} />
    </>
  );
}
