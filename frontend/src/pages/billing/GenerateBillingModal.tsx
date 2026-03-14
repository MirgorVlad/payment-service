import { useEffect, useState } from 'react';
import { Modal, Form, Select, DatePicker } from 'antd';
import dayjs from 'dayjs';
import type { Workspace } from '../../types/workspace';
import type { TimeInterval } from '../../types/billing';
import * as workspacesApi from '../../api/workspaces';

interface Props {
  open: boolean;
  onOk: (workspaceId: number, interval: TimeInterval) => Promise<void>;
  onCancel: () => void;
}

export default function GenerateBillingModal({ open, onOk, onCancel }: Props) {
  const [form] = Form.useForm<{ workspaceId: number; range: [dayjs.Dayjs, dayjs.Dayjs] }>();
  const [workspaces, setWorkspaces] = useState<Workspace[]>([]);

  useEffect(() => {
    if (open) workspacesApi.getAll().then(setWorkspaces);
  }, [open]);

  const handleOk = async () => {
    const { workspaceId, range } = await form.validateFields();
    const interval: TimeInterval = {
      startTime: range[0].format('YYYY-MM-DDTHH:mm:ss'),
      endTime: range[1].format('YYYY-MM-DDTHH:mm:ss'),
    };
    await onOk(workspaceId, interval);
    form.resetFields();
  };

  const workspaceOptions = workspaces.map((w) => ({ label: `${w.host} (${w.email})`, value: w.id }));

  return (
    <Modal open={open} title="Generate Billing" onOk={handleOk} onCancel={onCancel} destroyOnHide>
      <Form form={form} layout="vertical">
        <Form.Item name="workspaceId" label="Workspace" rules={[{ required: true }]}>
          <Select options={workspaceOptions} />
        </Form.Item>
        <Form.Item name="range" label="Billing Period" rules={[{ required: true }]}>
          <DatePicker.RangePicker showTime style={{ width: '100%' }} />
        </Form.Item>
      </Form>
    </Modal>
  );
}
