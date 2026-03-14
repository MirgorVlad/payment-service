import { Form, Input, Button, Card, Typography, message } from 'antd';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import * as authApi from '../api/auth';

export default function LoginPage() {
  const navigate = useNavigate();
  const { login } = useAuth();
  const [messageApi, contextHolder] = message.useMessage();

  const onFinish = async (values: { email: string; password: string }) => {
    try {
      const data = await authApi.signin(values);
      login(data.token, data.role);
      navigate('/workspaces', { replace: true });
    } catch {
      messageApi.error('Invalid credentials');
    }
  };

  return (
    <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '100vh', background: '#f0f2f5' }}>
      {contextHolder}
      <Card style={{ width: 360 }}>
        <Typography.Title level={3} style={{ textAlign: 'center', marginBottom: 24 }}>
          Payment Service
        </Typography.Title>
        <Form layout="vertical" onFinish={onFinish}>
          <Form.Item name="email" label="Email" rules={[{ required: true, type: 'email' }]}>
            <Input />
          </Form.Item>
          <Form.Item name="password" label="Password" rules={[{ required: true }]}>
            <Input.Password />
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit" block>
              Sign In
            </Button>
          </Form.Item>
        </Form>
      </Card>
    </div>
  );
}
