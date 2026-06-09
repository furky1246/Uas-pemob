<?php

require_once 'db.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    jsonResponse(['success' => false, 'message' => 'Method not allowed'], 405);
}

$body = json_decode(file_get_contents('php://input'), true);

$userId                = filter_var($body['user_id']                ?? null, FILTER_VALIDATE_INT);
$totalScore            = filter_var($body['total_score']            ?? null, FILTER_VALIDATE_INT);
$bestStreak            = filter_var($body['best_streak']            ?? null, FILTER_VALIDATE_INT);
$highestUnlockedSurah  = filter_var($body['highest_unlocked_surah'] ?? null, FILTER_VALIDATE_INT);
$currentSurah          = filter_var($body['current_surah']          ?? null, FILTER_VALIDATE_INT);
$currentVerse          = filter_var($body['current_verse']          ?? null, FILTER_VALIDATE_INT);

if (!$userId || $totalScore === false || $bestStreak === false ||
    !$highestUnlockedSurah || !$currentSurah || !$currentVerse) {
    jsonResponse(['success' => false, 'message' => 'Field tidak lengkap atau tidak valid'], 400);
}

$db = getDB();

// INSERT ... ON DUPLICATE KEY UPDATE memanfaatkan UNIQUE KEY pada user_id
// Satu query untuk handle dua kasus: insert baru dan update existing
$stmt = $db->prepare('
    INSERT INTO user_progress
        (user_id, total_score, best_streak, highest_unlocked_surah, current_surah, current_verse)
    VALUES
        (?, ?, ?, ?, ?, ?)
    ON DUPLICATE KEY UPDATE
        total_score            = VALUES(total_score),
        best_streak            = VALUES(best_streak),
        highest_unlocked_surah = VALUES(highest_unlocked_surah),
        current_surah          = VALUES(current_surah),
        current_verse          = VALUES(current_verse)
');

$stmt->execute([
    $userId,
    $totalScore,
    $bestStreak,
    $highestUnlockedSurah,
    $currentSurah,
    $currentVerse,
]);

jsonResponse(['success' => true]);
