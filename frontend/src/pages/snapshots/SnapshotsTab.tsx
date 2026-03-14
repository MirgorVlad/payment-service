import { useEffect, useState } from 'react';
import { Button, Form, Input, Modal, Select, Space, Table, message } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import type { Snapshot, WorkspaceEntityType } from '../../types/snapshot';
import * as snapshotsApi from '../../api/snapshots';
import ConfirmDeleteButton from '../../components/ConfirmDeleteButton';

const entityTypeOptions = ['DEVICE', 'ASSET', 'CUSTOMER'].map((t) => ({ label: t, value: t }));

interface Props {
  workspaceId: number;
}

export default function SnapshotsTab({ workspaceId }: Props) {
  const [snapshots, setSnapshots] = useState<Snapshot[]>([]);
  const [modalOpen, setModalOpen] = useState(false);
  const [messageApi, contextHolder] = message.useMessage();
  const [form] = Form.useForm<{ workspaceEntityType: WorkspaceEntityType; count: number }>();

  const load = () => snapshotsApi.getAll(workspaceId).then(setSnapshots);

  useEffect(() => { load(); }, [workspaceId]);

  const handleAdd = async () => {
    const values = await form.validateFields();
    await snapshotsApi.create({ ...values, workspaceId });
    messageApi.success('Snapshot added');
    setModalOpen(false);
    form.resetFields();
    load();
  };

  const handleDelete = async (id: number) => {
    await snapshotsApi.remove(id);
    messageApi.success('Deleted');
    load();
  };

  const columns = [
    { title: 'Entity Type', dataIndex: 'workspaceEntityType', key: 'workspaceEntityType' },
    { title: 'Count', dataIndex: 'count', key: 'count' },
    { title: 'Snapshot Time', dataIndex: 'snapshotTime', key: 'snapshotTime' },
    {
      title: 'Actions',
      key: 'actions',
      render: (_: unknown, s: Snapshot) => (
        <ConfirmDeleteButton onConfirm={() => s.id && handleDelete(s.id)} />
      ),
    },
  ];

  return (
    <>
      {contextHolder}
      <Button icon={<PlusOutlined />} onClick={() => setModalOpen(true)} style={{ marginBottom: 12 }}>
        Add Snapshot
      </Button>
      <Table rowKey="id" dataSource={snapshots} columns={columns} pagination={false} />
      <Modal open={modalOpen} title="Add Snapshot" onOk={handleAdd} onCancel={() => setModalOpen(false)} destroyOnHide>
        <Form form={form} layout="vertical">
          <Form.Item name="workspaceEntityType" label="Entity Type" rules={[{ required: true }]}>
            <Select options={entityTypeOptions} />
          </Form.Item>
          <Form.Item name="count" label="Count" rules={[{ required: true }]}>
            <Input type="number" min={1} />
          </Form.Item>
        </Form>
      </Modal>
    </>
  );
}
