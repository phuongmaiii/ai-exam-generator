import { useState } from 'react';
import { Table, InputNumber, Button, Space, Card } from 'antd';
import api from '../api';

export default function MatchResults() {
  const [jobId, setJobId] = useState(null);
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(false);

  const loadMatches = async () => {
    if (!jobId) return;
    setLoading(true);
    try {
      const res = await api.get(`/match/job/${jobId}`);
      setData(res.data);
    } finally {
      setLoading(false);
    }
  };

  const columns = [
    { title: 'Candidate ID', dataIndex: 'candidateId' },
    { title: 'Match Score', dataIndex: 'score', render: (v) => (v * 100).toFixed(1) + '%' },
    { title: 'Thời gian', dataIndex: 'createdAt' },
  ];

  return (
    <Card title="Kết quả Match" style={{ maxWidth: 700, margin: '40px auto' }}>
      <Space style={{ marginBottom: 16 }}>
        <InputNumber placeholder="Job Posting ID" onChange={setJobId} />
        <Button type="primary" onClick={loadMatches} loading={loading}>Xem kết quả</Button>
      </Space>
      <Table columns={columns} dataSource={data} rowKey="id" />
    </Card>
  );
}