import { useNavigate } from 'react-router-dom';
import { ArrowLeftOutlined } from '@ant-design/icons';
import { useState, useEffect } from 'react';
import { Table, Card, message, Button, Tag } from 'antd';
import { useParams } from 'react-router-dom';
import api from '../api';

export default function HRMatches() {
  const { jobPostingId } = useParams(); // Lấy ID của Job từ thanh URL
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  // State quản lý phân trang
  const [pagination, setPagination] = useState({
    current: 1,      // Trang hiện tại (UI)
    pageSize: 10,    // Số ứng viên mỗi trang
    total: 0,        // Tổng số ứng viên
  });

  // Hàm gọi API lấy dữ liệu
  const fetchMatches = async (page = 1, pageSize = 10) => {
    setLoading(true);
    try {
      // Gọi API phân trang của Spring Boot (trừ đi 1 vì Spring Boot đếm từ 0)
      const res = await api.get(`/match/job/${jobPostingId}/ranked`, {
        params: {
          page: page - 1,
          size: pageSize
        }
      });
      
      // res.data là đối tượng Page<MatchResultDto> trả về từ Backend
      setData(res.data.content); 
      setPagination({
        ...pagination,
        current: page,
        total: res.data.totalElements, // Tổng số lượng bản ghi DB trả về
      });
    } catch (error) {
      message.error('Không thể tải danh sách ứng viên phù hợp.');
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  // Chạy lần đầu khi vào trang
  useEffect(() => {
    if (jobPostingId) {
      fetchMatches(1, pagination.pageSize);
    }
  }, [jobPostingId]);

  // Bắt sự kiện khi HR bấm chuyển trang trên bảng
  const handleTableChange = (newPagination) => {
    fetchMatches(newPagination.current, newPagination.pageSize);
  };

  // Định nghĩa các cột cho bảng
  const columns = [
    {
      title: 'Tên Ứng Viên',
      dataIndex: 'candidateName',
      key: 'candidateName',
      render: (text) => <b>{text || 'Chưa cập nhật'}</b>,
    },
    {
      title: 'Độ Phù Hợp (Match Score)',
      dataIndex: 'score',
      key: 'score',
      render: (score) => {
        const percentScore = Math.round(score * 100);
        let color = percentScore >= 80 ? 'green' : percentScore >= 50 ? 'orange' : 'red';
        return <Tag color={color}>{percentScore ? `${percentScore}%` : 'N/A'}</Tag>;
      },
    },
    {
  title: 'Hành động',
  key: 'action',
  render: (_, record) => (
    <Button type="link" onClick={() => navigate(`/hr/candidate/${record.candidateId}`)}>
      Xem Chi Tiết CV
    </Button>
  ),
}
  ];

  return (
    <Card 
  title={`Đề xuất ứng viên cho Công Việc ID: ${jobPostingId}`} 
  style={{ margin: '40px auto', maxWidth: 900 }}
  extra={
    <Button icon={<ArrowLeftOutlined />} onClick={() => navigate(-1)}>
      Quay lại
    </Button>
  }
>
      <Table
        columns={columns}
        dataSource={data}
        rowKey="matchId" // Khóa chính để Ant Design phân biệt các hàng
        pagination={{
          current: pagination.current,
          pageSize: pagination.pageSize,
          total: pagination.total,
          showSizeChanger: false, // Tắt tính năng cho phép user đổi số dòng (10/20/50) để cố định 10 dòng
        }}
        loading={loading}
        onChange={handleTableChange} // Kích hoạt hàm khi bấm sang trang 2, 3...
      />
    </Card>
  );
}