#!/bin/bash

OLD_PKG="com.example.isyara"
NEW_PKG="com.isyara.app"
OLD_DIR="com/example/isyara"
NEW_DIR="com/isyara/app"

echo "1. Mengganti teks package di semua file (Kotlin, XML, Gradle)..."
find . -type f \( -name "*.kt" -o -name "*.java" -o -name "*.xml" -o -name "*.gradle" -o -name "*.kts" \) -exec sed -i '' "s/$OLD_PKG/$NEW_PKG/g" {} +

echo "2. Memindahkan struktur folder source code..."
for src in app/src/main/java app/src/androidTest/java app/src/test/java; do
  if [ -d "$src/$OLD_DIR" ]; then
    mkdir -p "$src/$NEW_DIR"
    mv "$src/$OLD_DIR/"* "$src/$NEW_DIR/" 2>/dev/null
    # Hapus folder 'example' lama biar bersih
    rm -rf "$src/com/example" 
  fi
done

echo "Beres bos! Proses rename masal selesai."
