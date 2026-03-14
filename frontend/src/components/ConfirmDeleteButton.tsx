import { Button, Popconfirm } from 'antd';
import { DeleteOutlined } from '@ant-design/icons';

interface Props {
  onConfirm: () => void;
}

export default function ConfirmDeleteButton({ onConfirm }: Props) {
  return (
    <Popconfirm title="Delete this record?" onConfirm={onConfirm} okText="Yes" cancelText="No">
      <Button danger icon={<DeleteOutlined />} size="small" />
    </Popconfirm>
  );
}
