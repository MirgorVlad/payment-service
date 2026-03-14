import { useEffect, useState } from 'react';
import { Button, Space, Table, message, Typography } from 'antd';
import { PlusOutlined, SyncOutlined, CameraOutlined, EyeOutlined, EditOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import type { Workspace } from '../../types/workspace';
import * as workspacesApi from '../../api/workspaces';
import { useAuth } from '../../context/AuthContext';
import SyncStatusBadge from '../../components/SyncStatusBadge';
import ConfirmDeleteButton from '../../components/ConfirmDeleteButton';
import WorkspaceFormModal from './WorkspaceFormModal';

export default function WorkspacesPage() {
  const [workspaces, setWorkspaces] = useState<Workspace[]>([]);
  const [loading, setLoading] = useState(false);
  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState<Workspace | null>(null);
  const [messageApi, contextHolder] = message.useMessage();
  const { role } = useAuth();
  const navigate = useNavigate();
  const isAdmin = role === 'ADMIN';

  const load = async () => {
    setLoading(true);
    try {
      setWorkspaces(await workspacesApi.getAll());
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { load(); }, []);

  const handleCreate = () => { setEditing(null); setModalOpen(true); };
  const handleEdit = (ws: Workspace) => { setEditing(ws); setModalOpen(true); };

  const handleSave = async (values: Workspace) => {
    if (editing?.id) {
      await workspacesApi.update(editing.id, values);
      messageApi.success('Workspace updated');
    } else {
      await workspacesApi.create(values);
      messageApi.success('Workspace created');
    }
    setModalOpen(false);
    load();
  };

  const handleDelete = async (id: number) => {
    await workspacesApi.remove(id);
    messageApi.success('Deleted');
    load();
  };

  const handleSyncAll = async () => {
    await workspacesApi.syncAll();
    messageApi.success('Sync triggered');
    load();
  };

  const handleSnapshot = async () => {
    await workspacesApi.takeSnapshot();
    messageApi.success('Snapshots taken');
  };

  const columns = [
    { title: 'Host', dataIndex: 'host', key: 'host' },
    { title: 'Email', dataIndex: 'email', key: 'email' },
    { title: 'Currency', dataIndex: 'currency', key: 'currency' },
    { title: 'Strategy', dataIndex: 'pricingStrategy', key: 'pricingStrategy' },
    {
      title: 'Status',
      key: 'syncStatus',
      render: (_: unknown, ws: Workspace) => <SyncStatusBadge status={ws.syncStatus} />,
    },
    {
      title: 'Actions',
      key: 'actions',
      render: (_: unknown, ws: Workspace) => (
        <Space>
          <Button icon={<EyeOutlined />} size="small" onClick={() => navigate(`/workspaces/${ws.id}`)}>
            Detail
          </Button>
          <Button icon={<EditOutlined />} size="small" onClick={() => handleEdit(ws)}>
            Edit
          </Button>
          <ConfirmDeleteButton onConfirm={() => ws.id && handleDelete(ws.id)} />
        </Space>
      ),
    },
  ];

  return (
    <>
      {contextHolder}
      <Space style={{ marginBottom: 16 }} wrap>
        <Typography.Title level={4} style={{ margin: 0 }}>Workspaces</Typography.Title>
        <Button type="primary" icon={<PlusOutlined />} onClick={handleCreate}>
          New
        </Button>
        {isAdmin && (
          <>
            <Button icon={<SyncOutlined />} onClick={handleSyncAll}>Sync All</Button>
            <Button icon={<CameraOutlined />} onClick={handleSnapshot}>Take Snapshot</Button>
          </>
        )}
      </Space>
      <Table
        rowKey="id"
        loading={loading}
        dataSource={workspaces}
        columns={columns}
        pagination={{ pageSize: 10 }}
      />
      <WorkspaceFormModal
        open={modalOpen}
        initial={editing}
        onOk={handleSave}
        onCancel={() => setModalOpen(false)}
      />
    </>
  );
}
