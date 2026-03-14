import { useEffect, useState } from 'react';
import { Button, Form, Input, Modal, Select, Space, Table, message } from 'antd';
import { PlusOutlined, EditOutlined } from '@ant-design/icons';
import type { Price } from '../../types/price';
import type { WorkspaceEntityType } from '../../types/snapshot';
import * as pricesApi from '../../api/prices';
import ConfirmDeleteButton from '../../components/ConfirmDeleteButton';

const entityTypeOptions = ['DEVICE', 'ASSET', 'CUSTOMER'].map((t) => ({ label: t, value: t }));

interface Props {
  workspaceId: number;
}

export default function PricesTab({ workspaceId }: Props) {
  const [prices, setPrices] = useState<Price[]>([]);
  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState<Price | null>(null);
  const [messageApi, contextHolder] = message.useMessage();
  const [form] = Form.useForm<{ workspaceEntityType: WorkspaceEntityType; price: number }>();

  const load = () => pricesApi.getAll(workspaceId).then(setPrices);

  useEffect(() => { load(); }, [workspaceId]);

  const openCreate = () => { setEditing(null); form.resetFields(); setModalOpen(true); };
  const openEdit = (p: Price) => { setEditing(p); form.setFieldsValue(p); setModalOpen(true); };

  const handleSave = async () => {
    const values = await form.validateFields();
    if (editing?.id) {
      await pricesApi.update(editing.id, { ...values, workspaceId });
      messageApi.success('Price updated');
    } else {
      await pricesApi.create({ ...values, workspaceId });
      messageApi.success('Price added');
    }
    setModalOpen(false);
    load();
  };

  const handleDelete = async (id: number) => {
    await pricesApi.remove(id);
    messageApi.success('Deleted');
    load();
  };

  const columns = [
    { title: 'Entity Type', dataIndex: 'workspaceEntityType', key: 'workspaceEntityType' },
    { title: 'Price', dataIndex: 'price', key: 'price' },
    {
      title: 'Actions',
      key: 'actions',
      render: (_: unknown, p: Price) => (
        <Space>
          <Button icon={<EditOutlined />} size="small" onClick={() => openEdit(p)} />
          <ConfirmDeleteButton onConfirm={() => p.id && handleDelete(p.id)} />
        </Space>
      ),
    },
  ];

  return (
    <>
      {contextHolder}
      <Button icon={<PlusOutlined />} onClick={openCreate} style={{ marginBottom: 12 }}>
        Add Price
      </Button>
      <Table rowKey="id" dataSource={prices} columns={columns} pagination={false} />
      <Modal
        open={modalOpen}
        title={editing ? 'Edit Price' : 'Add Price'}
        onOk={handleSave}
        onCancel={() => setModalOpen(false)}
        destroyOnHide
      >
        <Form form={form} layout="vertical">
          <Form.Item name="workspaceEntityType" label="Entity Type" rules={[{ required: true }]}>
            <Select options={entityTypeOptions} />
          </Form.Item>
          <Form.Item name="price" label="Price" rules={[{ required: true }]}>
            <Input type="number" min={0.01} step={0.01} />
          </Form.Item>
        </Form>
      </Modal>
    </>
  );
}
