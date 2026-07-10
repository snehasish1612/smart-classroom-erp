import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

const API = 'http://localhost:8080';

async function fetchUsers() {
  const res = await fetch(API + '/api/users');
  return res.json();
}

async function main() {
  const users = await fetchUsers();
  const student = users.find(u => u.email === 'student@smartclassroom.local');
  const teacher = users.find(u => u.email === 'teacher@smartclassroom.local' || u.email === 'ananya@faculty.local');
  if (!student || !teacher) {
    console.error('Required seeded users not found. Found users:', users.map(u=>u.email));
    process.exit(1);
  }

  console.log('Using student id', student.id, 'teacher id', teacher.id);

  const client = new Client({
    webSocketFactory: () => new SockJS(API + '/ws'),
    reconnectDelay: 5000,
  });

  client.onConnect = () => {
    console.log('STOMP connected');
    client.subscribe(`/user/${teacher.id}/queue/messages`, (msg) => {
      console.log('Real-time message for teacher:', msg.body);
    });
    client.subscribe(`/user/${teacher.id}/queue/notifications`, (msg) => {
      console.log('Real-time notification for teacher:', msg.body);
    });

    // send a message from student to teacher
    setTimeout(async () => {
      const payload = { senderId: student.id, receiverId: teacher.id, message: 'Hello from automated test' };
      const r = await fetch(API + '/api/chat/messages', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload) });
      const body = await r.json();
      console.log('POST result:', r.status, body);
    }, 1000);
  };

  client.onStompError = (frame) => {
    console.error('Broker reported error: ' + frame.headers['message']);
    console.error('Additional details: ' + frame.body);
  };

  client.activate();

  // exit after 8s
  setTimeout(() => {
    client.deactivate();
    console.log('Exiting test client');
    process.exit(0);
  }, 8000);
}

main().catch(err => { console.error(err); process.exit(1); });
