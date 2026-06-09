<?php

require_once 'db.php';

if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    jsonResponse(['success' => false, 'message' => 'Method not allowed'], 405);
}

$chapterId = filter_input(INPUT_GET, 'chapter_id', FILTER_VALIDATE_INT);

if (!$chapterId || $chapterId < 1) {
    jsonResponse(['success' => false, 'message' => 'Parameter chapter_id tidak valid'], 400);
}

$db   = getDB();
$stmt = $db->prepare(
    'SELECT number, text_uthmani FROM verses WHERE id_chapter = ? ORDER BY number ASC'
);
$stmt->execute([$chapterId]);
$rows = $stmt->fetchAll();

if (empty($rows)) {
    jsonResponse(['success' => false, 'message' => 'Chapter tidak ditemukan'], 404);
}

$verses = array_map(fn($row) => [
    'verse_number' => (int) $row['number'],
    'text'         => $row['text_uthmani'],
], $rows);

jsonResponse($verses);
