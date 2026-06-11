<?php
// register.php
require_once __DIR__ . '/config.php';

// Verify POST request
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    sendResponse([
        "success" => false,
        "message" => "Method not allowed. Use POST."
    ], 405);
}

// Get JSON input
$input = getJsonInput();
$name = isset($input['name']) ? trim($input['name']) : '';
$email = isset($input['email']) ? trim($input['email']) : '';
$password = isset($input['password']) ? trim($input['password']) : '';

// Validation
if (empty($name) || empty($email) || empty($password)) {
    sendResponse([
        "success" => false,
        "message" => "Nama, email, dan password wajib diisi."
    ], 400);
}

if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
    sendResponse([
        "success" => false,
        "message" => "Format email tidak valid."
    ], 400);
}

try {
    // Check if email already exists
    $stmt = $pdo->prepare("SELECT id FROM users WHERE email = ?");
    $stmt->execute([$email]);
    if ($stmt->fetch()) {
        sendResponse([
            "success" => false,
            "message" => "Email sudah terdaftar."
        ], 409);
    }

    // Hash password securely
    $hashedPassword = password_hash($password, PASSWORD_DEFAULT);

    // Start transaction to ensure user and default progress are both created
    $pdo->beginTransaction();

    // Insert user
    $stmt = $pdo->prepare("INSERT INTO users (name, email, password) VALUES (?, ?, ?)");
    $stmt->execute([$name, $email, $hashedPassword]);
    $userId = (int)$pdo->lastInsertId();

    // Initialize default progress for the user
    $stmt = $pdo->prepare("INSERT INTO user_progress (user_id, total_score, best_streak, highest_unlocked_surah, current_surah, current_verse) VALUES (?, 0, 0, 1, 1, 1)");
    $stmt->execute([$userId]);

    $pdo->commit();

    // Return authentication response matching AuthResponse model
    sendResponse([
        "success" => true,
        "user_id" => $userId,
        "username" => $name,
        "email" => $email,
        "message" => "Registrasi berhasil"
    ], 201);

} catch (Exception $e) {
    if ($pdo->inTransaction()) {
        $pdo->rollBack();
    }
    sendResponse([
        "success" => false,
        "message" => "Terjadi kesalahan server: " . $e->getMessage()
    ], 500);
}
