import { useEffect, useState } from 'react';
import { Table, Tag } from 'antd';
import api from '../api'; // Đường dẫn tới file config axios của bạn

export default function MatchTable({ apiPath, columnsConfig }) {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(false);
  const [pagination, setPagination] = useState({ current: 1, pageSize: 20, total: 0 });

  const fetchData = async (page, pageSize) => {
    setLoading(true);
    try {
      const res = await api.get(apiPath, {
        params: { page: page - 1, size: pageSize }, // Spring Boot đếm trang từ 0
      });
      setData(res.data.content);
      setPagination({ current: page, pageSize, total: res.data.totalElements });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData(1, 20);
  }, [apiPath]);

  const scoreColumn = {
    title: 'Độ phù hợp',
    dataIndex: 'score',
    render: (score) => {
      const percent = (score * 100).toFixed(1);
      const color = score >= 0.85 ? 'green' : score >= 0.7 ? 'blue' : 'default';
      return <Tag color={color}>{percent}%</Tag>;
    },
    sorter: (a, b) => a.score - b.score,
    defaultSortOrder: 'descend',
  };

  return (
    <Table
      rowKey="matchId"
      loading={loading}
      dataSource={data}
      columns={[...columnsConfig, scoreColumn]}
      pagination={{
        ...pagination,
        showSizeChanger: true,
        pageSizeOptions: [10, 20, 50, 100],
        showTotal: (total) => `Tổng ${total} kết quả`,
      }}
      onChange={(p) => fetchData(p.current, p.pageSize)}
    />
  );
}