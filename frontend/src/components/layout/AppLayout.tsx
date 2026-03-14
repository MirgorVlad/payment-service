import { Layout, Menu, Button, Typography } from 'antd';
import { DatabaseOutlined, DollarOutlined, LogoutOutlined } from '@ant-design/icons';
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

const { Sider, Header, Content } = Layout;

export default function AppLayout() {
  const navigate = useNavigate();
  const location = useLocation();
  const { role, logout } = useAuth();

  const selectedKey = location.pathname.startsWith('/billing') ? 'billing' : 'workspaces';

  const handleLogout = () => {
    logout();
    navigate('/login', { replace: true });
  };

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider theme="dark" width={200}>
        <div style={{ padding: '16px', color: 'white', fontWeight: 'bold', fontSize: 16 }}>
          PaymentService
        </div>
        <Menu
          theme="dark"
          mode="inline"
          selectedKeys={[selectedKey]}
          onClick={({ key }) => navigate(`/${key}`)}
          items={[
            { key: 'workspaces', icon: <DatabaseOutlined />, label: 'Workspaces' },
            { key: 'billing', icon: <DollarOutlined />, label: 'Billing' },
          ]}
        />
      </Sider>
      <Layout>
        <Header
          style={{
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'flex-end',
            gap: 12,
            background: '#fff',
            padding: '0 24px',
          }}
        >
          <Typography.Text type="secondary">Role: {role}</Typography.Text>
          <Button icon={<LogoutOutlined />} onClick={handleLogout}>
            Logout
          </Button>
        </Header>
        <Content style={{ margin: 24, padding: 24, background: '#fff', borderRadius: 8 }}>
          <Outlet />
        </Content>
      </Layout>
    </Layout>
  );
}
