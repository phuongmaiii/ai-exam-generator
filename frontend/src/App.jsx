import { BrowserRouter, Routes, Route } from 'react-router-dom';
import RoleSelect from './pages/RoleSelect';
import UploadCv from './pages/UploadCv'; // Tùy chỉnh đúng tên file của bạn
import CandidateMatches from './pages/CandidateMatches';
import CreateJob from './pages/CreateJob'; // Tùy chỉnh đúng tên file của bạn
import HRMatches from './pages/HRMatches';

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<RoleSelect />} />

        {/* Luồng Ứng viên */}
        <Route path="/candidate" element={<UploadCv />} />
        <Route path="/candidate/matches/:candidateId" element={<CandidateMatches />} />

        {/* Luồng HR */}
        <Route path="/hr" element={<CreateJob />} />
        <Route path="/hr/matches/:jobPostingId" element={<HRMatches />} />
      </Routes>
    </BrowserRouter>
  );
}