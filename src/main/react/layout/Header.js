import React, {useContext, useEffect, useState} from 'react';
import '../../resources/static/css/common/Header.css';
import {FaBell, FaCommentDots, FaEnvelope} from 'react-icons/fa';
import Messenger from '../components/messenger/Messenger'
import {MessengerContext} from '../context/MessengerContext';
import '../../resources/static/css/messenger/Messenger.css';
import "../../resources/static/css/messenger/Chat.css";
import "../../resources/static/css/messenger/Note.css";
import "../../resources/static/css/messenger/Info.css";
import SockJS from "sockjs-client";
import {Stomp} from '@stomp/stompjs';
import {useMessengerHooks} from "../components/messenger/useMessengerHooks";
import ReceivedNoteModal from "../components/messenger/ReceivedNoteModal";


function Header() {

    const {
        isMessengerOpen,
        setMessengerOpen
    } = useContext(MessengerContext);

    const {
        noteList,
        setNoteList,
    } = useMessengerHooks();

    const handleCloseReceivedNoteModal = () => setNewNote(null); // 수신 쪽지 모달 닫기
    const [newNote, setNewNote] = useState(null); // 수신 쪽지 state

    // 메신저 토글
    const toggleMessenger = () => {
        setMessengerOpen(!isMessengerOpen);
    };

    const handleEmailClick = () => {
        window.location.href = '/receivedMail';
    }

    // 전역 웹소켓 연결 설정 (SockJS와 Stomp)
    useEffect(() => {
        const socket = new SockJS('http://localhost:8787/talk');
        const stompClient = Stomp.over(socket);

        stompClient.connect(
            {},
            () => {
                console.log("WebSocket 연결 성공");

                // // 상태 업데이트 구독 - /topic/status
                // stompClient.subscribe('/topic/status', (statusResponse) => {
                //     const statusUpdate = statusResponse.body;
                //     setTreeData(prevData => updateTreeWithNewStatus(prevData, statusUpdate));
                //     console.log("상태 업데이트:", statusUpdate);
                // })
                //
                // // 상태 메시지 업데이트 구독 - /topic/statusMessage
                // stompClient.subscribe('/topic/statusMessage', (statusMessageResponse) => {
                //     const statusMessageUpdate = statusMessageResponse.body;
                //     console.log("상태 메시지 업데이트:", statusMessageUpdate);
                // });

                // 수신 쪽지 구독 - /user/queue/note
                stompClient.subscribe('/user/queue/note', (noteResponse) => {
                    const receivedNote = JSON.parse(noteResponse.body);
                    console.log("수신 쪽지:", receivedNote); // senderId, messageContent, scheduledDate, messageReceiverIds

                    setNoteList((prevNoteList) => [receivedNote, ...prevNoteList]);
                    setNewNote(receivedNote);
                });
            },
            (error) => {
                console.log("WebSocket 연결 오류:", error);
            }
        );

        stompClient.reconnectDelay = 10000;

        stompClient.activate();

        return () => {
            stompClient.deactivate()
                .then(() => console.log("WebSocket 연결 해제 성공"))
                .catch((error) => console.log("WebSocket 해제 오류", error));
        };
    }, []);


    // // 디버깅용
    // useEffect(() => {
    //     if (newNote) {
    //         console.log("모달에 떠야 할 새로운 쪽지:", newNote);
    //     }
    // },[newNote]);

    return (
        <header>
            <div className="header-container">
                <div className="logo">
                    <a href="/main"><img src="/img/logo2.png"
                                         alt="IKEA Logo"/><span>Erpenterprise Resource  Planning</span></a>
                </div>
                <div className="header-icons">
                    <FaEnvelope className="header-icon mail" title="메일" onClick={handleEmailClick}/>
                    <FaCommentDots className="header-icon messenger" title="메신저" onClick={toggleMessenger}/>
                    <FaBell className="header-icon alarm" title="알림"/>
                </div>
            </div>
            <div className="bottom-border"></div>

            {/* 메신저 컴포넌트 */}
            <Messenger isOpen={isMessengerOpen} toggleMessenger={toggleMessenger}/>

            {/* 쪽지 수신 모달창 */}
            {newNote && (
                <ReceivedNoteModal
                    note={newNote}
                    onClose={handleCloseReceivedNoteModal}
                />
            )}
        </header>
    );
}

export default Header;
