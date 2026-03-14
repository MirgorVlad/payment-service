import { Modal, Form, Input, Select } from 'antd';
import { useEffect } from 'react';
import type { Workspace } from '../../types/workspace';

interface Props {
  open: boolean;
  initial?: Workspace | null;
  onOk: (values: Workspace) => Promise<void>;
  onCancel: () => void;
}

const currencyOptions = ['UAH', 'USD', 'EUR'].map((c) => ({ label: c, value: c }));
const strategyOptions = ['MAX', 'AVG', 'LATEST'].map((s) => ({ label: s, value: s }));

export default function WorkspaceFormModal({ open, initial, onOk, onCancel }: Props) {
  const [form] = Form.useForm<Workspace>();

  useEffect(() => {
    if (open) {
      form.setFieldsValue(initial ?? { email: '', host: '', password: '', currency: 'USD', pricingStrategy: 'MAX' });
    }
  }, [open, initial, form]);

  const handleOk = async () => {
    const values = await form.validateFields();
    await onOk(values);
    form.resetFields();
  };

  return (
    <Modal
      open={open}
      title={initial ? 'Edit Workspace' : 'New Workspace'}
      onOk={handleOk}
      onCancel={onCancel}
      destroyOnHide
    >
      <Form form={form} layout="vertical">
        <Form.Item name="email" label="ThingsBoard Email" rules={[{ required: true, type: 'email' }]}>
          <Input />
        </Form.Item>
        <Form.Item name="host" label="Host URL" rules={[{ required: true }]}>
          <Input placeholder="http://..." />
        </Form.Item>
        <Form.Item name="password" label="Password" rules={[{ required: true }]}>
          <Input.Password />
        </Form.Item>
        <Form.Item name="currency" label="Currency" rules={[{ required: true }]}>
          <Select options={currencyOptions} />
        </Form.Item>
        <Form.Item name="pricingStrategy" label="Pricing Strategy" rules={[{ required: true }]}>
          <Select options={strategyOptions} />
        </Form.Item>
      </Form>
    </Modal>
  );
}
