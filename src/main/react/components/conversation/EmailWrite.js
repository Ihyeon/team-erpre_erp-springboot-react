import React, { useRef, useState } from 'react';
import { BrowserRouter } from "react-router-dom";
import ReactDOM from 'react-dom/client';
import '../../../resources/static/css/conversation/EmailWrite.css'
import Layout from "../../layout/Layout";
import ReactQuill from "react-quill"; // 본문 글 편집기
import 'react-quill/dist/quill.snow.css'; // 편집기 기본 스타일
import axios from 'axios';
import { update } from 'lodash';


// 컴포넌트
function EmailWrite() {

  // 수신자
  const [to, setTo] = useState('');
  // 제목
  const [subject, setSubject] = useState('');
  // 본문
  const [text, setText] = useState('');
  // 파일 첨부
  const [files, setFiles] = useState([]);
  // 첨부된 파일 이름 
  const [fileNames, setFileNames] = useState([]);
  // 전송 중 로딩 상태 default 값 필요
  const [loading, setLoading] = useState(false);
  // reactQuill은 ref로 
  const quillRef = useRef(null);

  // 에러 메시지 상태
  const [errors, setErrors] = useState({
    to: '',
    subject: '',
    text: ''
  });

  // 공백일 시 에러 메세지
  const validateForm = () => {
    const newErrors = {
      to: '',
      subject: '',
      text: ''
    };
    let isValid = true;

    if (!to) {
      newErrors.to = '받는사람 이메일을 입력해주세요';
      isValid = false;
    }
    if (!subject) {
      newErrors.subject = '제목을 입력해주세요';
      isValid = false;
    }
    setErrors(newErrors);
    return isValid;
  }

  // 받는사람 인풋 공백 시
  const handleToChange = (e) => {
    setTo(e.target.value);
    if (errors.to) {
      setErrors((prevErrors) => ({ //prevErrors 는 현재 모든 에러 메시지를 포함
        ...prevErrors,
        to: ''
      }));
    }
  };

  // 제목 인풋 공백 시
  const handleSubjectChange = (e) => {
    setSubject(e.target.value);
    if (errors.subject) {
      setErrors((prevErrors) => ({
        ...prevErrors,
        subject: ''
      }));
    }
  };


  //파일 리스트 업데이트 함수 e 사용 / 파일 첨부 state 사용
  const handleFileList = (e) => {
    const newFiles = Array.from(e.target.files); // 파일들을 배열로 만듦
    setFiles((prevFiles) => [...prevFiles, ...newFiles]);
    setFileNames((prevFileNames) => [
      ...prevFileNames,
      ...newFiles.map(file => file.name) // 기존파일이 선택된 상태에서 새로운 파일 선택
    ]);  // 배열을 순회하며, 이름만 추출 새로운 배열 생성 
  };

  //선택된 첨부파일 삭제
  const removeFile = (index) => {
    setFileNames((prevFiles) => prevFiles.filter((_, i) => i !== index));
  };
  


  //보내기 함수 어싱크 사용 /
  // 로딩 상태 활성화 / 
  //이메일 데이터를 multipart 형식으로 서버에 전송하기 위해 사용 폼데이타를 새로은 변수 선언
  // 폼데이타에 수신자 이메일 주소 추가 / 
  // 폼데이타에 이메일 제목 추가 / 
  // 폼데이타에 이메일 본문 추가 / 
  const handleSendEmail = async () => {

    if (!validateForm()) {    //이메일 공백 유효성 검사
      return;
    }
    setLoading(true);
    const formData = new FormData(); // formData는 파일 업로드 폼데이터 저장하기 위한 객체
    formData.append('to', to); // append : 폼데이터에 ('to'라는 키 추가, to라는 현재의 상태값)
    formData.append('subject', subject);
    formData.append('text', text);

    //파일 첨부 처리 반복문으로 폼데이타에 저장 / 첨부파일은 여러개
    for (let i = 0; i < files.length; i++) {
      formData.append('files', files[i])
    }

    // try 구문 안에
    // 어웨이트를 포스트 방식으로 전송('/엔드포인트 url', 데이터 , 폼데이터를 보냄{
    // 헤더 안에 컨텐츠 타입을 설정함 : 첨부파일의 방식 / 폼데이터
    // 성공처리 알람
    // 오류 처리 알람
    // 로딩상태 아래꺼 가져오기

    try {
      const response = await axios.post('/api/email/send', formData, {
        headers: {
          'Content-Type': 'multipart/form-data' // multipart 여러 텍스트, 파일 전송 가능한 타입
        }
      });
      window.showToast("메일 전송이 완료되었습니다.")
    } catch (error) {
      console.log("메일 전송에 실패했습니다.", error);
      window.showToast("메일 전송에 실패했습니다", 'error', 3000);
    } finally {
      setLoading(false);   // 이메일 전송 후 로딩 상태값 변경
    }


  }

  return (
    <Layout currentMenu="emailWrite">

      <div className="email-compose-container">
        <div className="email-compose-container">
          {/* 상단 메뉴 */}

          {/* 받는 사람*/}
          <div className="email-field">
            <label htmlFor="to">받는 사람</label>
            <button><i className="bi bi-person-plus"></i></button>
            <input
              type="text"
              id="to"
              value={to}
              onChange={(e) => handleToChange(e)}
              placeholder='수신자'
            // className={errors.to ? 'field_error' : 'field_ok'}
            />
            {/* 공백 유효성 검사 */}
            {errors.to && (
              <p className='field_error_msg'>
                <i className="bi bi-exclamation-circle-fill"></i>
                {errors.to}
              </p>
            )}
          </div>

          {/* 제목 */}
          <div className="email-field">
            <label htmlFor="subject">제목</label>
            <input
              className='subject-input'
              type="text"
              id="subject"
              placeholder="제목"
              value={subject}
              onChange={(e) => handleSubjectChange(e)}
            // className={errors.subject ? 'field_error' : 'field_ok'}
            />
            {/* 공백 유효성 검사 */}
            {errors.subject && (
              <p className='field_error_msg'>
                <i className="bi bi-exclamation-circle-fill"></i>
                {errors.subject}
              </p>
            )}
          </div>

          {/* 파일 첨부 */}
          <label htmlFor="file-upload" className="file-label"> {/*htmlFor: file-upload라는 id의 input과 연결됨*/}
            <div className="file-attachment">

              <input
                type="file"
                id="file-upload"
                accept='image/png, image/jpeg, application/pdf'
                className='file-upload-input'
                multiple //여러파일 선택
                onChange={handleFileList}
              />
              <div>
                {fileNames.length > 0 ? (
                  fileNames.map((file, index) => (
                    <div key={index}>
                      <span>{file.name}</span>
                      <button onClick={() => removeFile(index)}>
                        <i class="bi bi-trash3"></i>
                      </button>
                    </div>
                  ))
                ) : (
                  '파일첨부'
                )}
              </div>
            </div>
          </label>

          {/* 이메일 본문 + 편집기  */}
          <div className='email-text'>
            <ReactQuill
              ref={quillRef} //ReactQuill에 ref 추가
              className='email-textBody'
              value={text}
              onChange={(value) => setText(value)} // reactQuill 같은 서드파티 편집기는 html 구조를 반환함 바로 value로 받음
            />
          </div>

          <div className="email-footer">
            <div>
              {/* 1. 버튼 안에 로딩창 */}
              <button
                onClick={handleSendEmail}
                disabled={loading}
              >
                <i className="bi bi-send icon-margin"></i>
                {loading ? '메일 전송중 ..' : '보내기'}
              </button>
              <a href="#">임시저장</a>
            </div>
          </div>

        </div>

      </div>
    </Layout>
  );
}

const root = ReactDOM.createRoot(document.getElementById('root')); // 루트 DOM 요소에 리액트 컴포넌트를 랜더링
root.render(
  <BrowserRouter> {/*리액트 라우터를 사용하여 클라이언트 사이드 라우팅 지원*/}
    <EmailWrite /> {/*컴포넌트 랜더링*/}
  </BrowserRouter>
)