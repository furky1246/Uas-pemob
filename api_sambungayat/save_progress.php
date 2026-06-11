<?php
// save_progress.php
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
$userId = isset($input['user_id']) ? (int)$input['user_id'] : 0;
$totalScore = isset($input['total_score']) ? (int)$input['total_score'] : 0;
$bestStreak = isset($input['best_streak']) ? (int)$input['best_streak'] : 0;
$highestUnlockedSurah = isset($input['highest_unlocked_surah']) ? (int)$input['highest_unlocked_surah'] : 1;
$currentSurah = isset($input['current_surah']) ? (int)$input['current_surah'] : 1;
$currentVerse = isset($input['current_verse']) ? (int)$input['current_verse'] : 1;

if ($userId <= 0) {
    sendResponse([
        "success" => false,
        "message" => "User ID tidak valid atau kosong."
    ], 400);
}

try {
    // Perform UPSERT (Insert or Update on Duplicate Key)
    $stmt = $pdo->prepare("
        INSERT INTO user_progress (user_id, total_score, best_streak, highest_unlocked_surah, current_surah, current_verse)
        VALUES (?, ?, ?, ?, ?, ?)
        ON DUPLICATE KEY UPDATE
            total_score = VALUES(total_score),
            best_streak = GREATEST(best_streak, VALUES(best_streak)),
            highest_unlocked_surah = GREATEST(highest_unlocked_surah, VALUES(highest_unlocked_surah)),
            current_surah = VALUES(current_surah),
            current_verse = VALUES(current_verse)
    ");
    
    $stmt->execute([
        $userId,
        $totalScore,
        $bestStreak,
        $highestUnlockedSurah,
        $currentSurah,
        $currentVerse
    ]);

    sendResponse([
        "success" => true,
        "message" => "Progress berhasil disimpan."
    ]);
} catch (Exception $e) {
    sendResponse([
        "success" => false,
        "message" => "Terjadi kesalahan server: " . $e->getMessage()
    ], 500);
}
