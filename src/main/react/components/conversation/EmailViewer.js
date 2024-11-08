import React, { useEffect, useState } from 'react';
import { BrowserRouter, useParams } from "react-router-dom";
import ReactDOM from 'react-dom/client';
import DOMPurify from 'dompurify';
import '../../../resources/static/css/conversation/EmailViewer.css'
import Layout from "../../layout/Layout";
import axios from 'axios';


function EmailViewer() {

  const { emailNmS } = useParams(); // URL에서 emailNmS 가져옴
  const [emailData, setEmailData] = useState(null); // 이메일 데이터



  //선택한 메일을 불러옴
  useEffect(() => {
    const fetchEmailDetail = async () => {
      try {
        const response = await axios.get(`/api/email/read/${emailNmS}`);
        setEmailData(response.data);
      } catch (error) {
        console.error('메일을 읽어오지 못하였습니다.', error);
      }
    };

    fetchEmailDetail();
  }, [emailNmS]);
  console.log(emailData);

  //로그인한 계정의 이메일 / 이름을 가져와서 보낸사람에 넣기위함
  const senderEmail = localStorage.getItem('employeeEmail');
  const senderName = localStorage.getItem('employeeName');

  // 조건부 렌더링: emailData가 null이 아닐 때만 렌더링 // 이메일 데이터를 불러오기 전에 페이지가 로드되면 오류가 발생하기 때문에
  if (!emailData) {
    return <div>이메일 데이터를 불러오는 중입니다...</div>;
  }

  // 이메일 내용부분 xxs공격 방지위해 Dompurify 정화
  const sanitizeHTML = (html) => {
    return DOMPurify.sanitize(html);
  };


  return (

    <Layout currentMenu="emailViewer">
      <div className="email-viewer-container">

        <div className="email-actions">
          <a href="#" className="action-link">답장</a>
          <a href="#" className="action-link">전달</a>
          <a href="#" className="action-link delete">삭제</a>
        </div>

        <h1 className="email-subject">{emailData.emailSubjectS}</h1>

        <div className="email-meta">
          <span>{new Date(emailData.emailDateS).toLocaleString()}</span>
        </div>

        <div className="email-info">
          <p>보낸 사람: {senderName} <span className="email-address"><a href="#">{senderEmail}</a></span> </p>
          <p>받는 사람: 이순신 <span className="email-address"><a href="#">{emailData.emailAddrReceiveS}</a></span> </p>
        </div>

        <div className="email-text"
          dangerouslySetInnerHTML={{ __html: sanitizeHTML(emailData?.emailTextS || '내용 없음') }}
        ></div>
      </div>

    </Layout>

  );
}

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <BrowserRouter>
    <EmailViewer />
  </BrowserRouter>
);