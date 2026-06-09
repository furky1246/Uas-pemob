<?php

require_once 'db.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    jsonResponse(['success' => false, 'message' => 'Method not allowed'], 405);
}

$body     = json_decode(file_get_contents('php://input'), true);
$email    = trim($body['email']    ?? '');
$password = trim($body['password'] ?? '');

if ($email === '' || $password === '') {
    jsonResponse(['success' => false, 'message' => 'Email dan password wajib diisi'], 400);
}

if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
    jsonResponse(['success' => false, 'message' => 'Format email tidak valid'], 400);
}

$db   = getDB();
$stmt = $db->prepare('SELECT id, name, password FROM users WHERE email = ? LIMIT 1');
$stmt->execute([$email]);
$user = $stmt->fetch();

if (!$user) {
    jsonResponse(['success' => false, 'message' => 'Email tidak terdaftar'], 404);
}

if (!password_verify($password, $user['password'])) {
    jsonResponse(['success' => false, 'message' => 'Password salah'], 401);
}

jsonResponse([
    'success'  => true,
    'user_id'  => (int) $user['id'],
    'username' => $user['name'],
]);
