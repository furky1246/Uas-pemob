<?php

require_once 'db.php';

if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    jsonResponse(['success' => false, 'message' => 'Method not allowed'], 405);
}

$db   = getDB();
$stmt = $db->query('SELECT id, name, number FROM juzs ORDER BY number ASC');
$rows = $stmt->fetchAll();

$juzList = array_map(fn($row) => [
    'id'     => (int) $row['id'],
    'name'   => $row['name'],
    'number' => (int) $row['number'],
], $rows);

jsonResponse($juzList);
