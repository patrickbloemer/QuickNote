# QuickNote

App minimalista de captura rápida de notas. Dark mode, estilo terminal, salva instantaneamente.

---

## Como abrir o app

### Opção 1 — Bixby Routines (recomendado para S23 FE)

O Android não permite que apps de terceiros interceptem o duplo pressionamento do botão Power diretamente — isso é reservado ao sistema. No Galaxy S23 FE, a forma oficial é via **Bixby Routines**:

1. Abra **Configurações** → **Recursos avançados** → **Bixby Routines**
2. Toque em **+** para criar nova rotina
3. Em **SE** (condição), selecione **Botão Liga/Desliga** → **Pressionar duas vezes**
4. Em **ENTÃO** (ação), selecione **Abrir app** → escolha **QuickNote**
5. Salve a rotina

> ⚠️ Se "Botão Liga/Desliga" não aparecer, vá em Configurações → Recursos avançados → Tecla lateral e verifique se está configurada para Bixby.

### Opção 2 — Tecla lateral (mais simples)

1. Vá em **Configurações** → **Recursos avançados** → **Tecla lateral**
2. Em **Pressionar duas vezes**, selecione **Abrir app**
3. Escolha **QuickNote**

> Esta é a forma mais direta. No S23 FE, a tecla lateral é o botão Power.

### Opção 3 — Widget na tela de bloqueio

Adicione o ícone do QuickNote como atalho na tela de bloqueio para acesso com 1 toque.

---

## Como usar

- Ao abrir, o teclado já aparece automaticamente
- Digite sua nota
- Pressione **Enter** ou o botão **→** para salvar
- A nota aparece na lista com horário e agrupamento por dia
- **Segurar** um item para deletá-lo

---

## Estrutura do projeto

```
QuickNote/
├── app/src/main/
│   ├── AndroidManifest.xml
│   ├── java/com/quicknote/app/
│   │   ├── MainActivity.kt          # Lista de notas
│   │   ├── QuickCaptureActivity.kt  # Captura rápida (abre com teclado)
│   │   └── NoteDatabase.kt          # SQLite local
│   └── res/
│       ├── layout/
│       │   ├── activity_main.xml
│       │   ├── activity_quick_capture.xml
│       │   ├── item_note.xml
│       │   └── item_header.xml
│       ├── values/
│       │   ├── colors.xml           # Paleta dark (#0A0A0C bg, #E8FF47 accent)
│       │   ├── themes.xml
│       │   └── strings.xml
│       ├── drawable/                # Ícones vetoriais
│       └── font/space_mono.xml     # Google Fonts (Space Mono)
├── build.gradle
└── settings.gradle
```

---

## Próximo passo — Supabase

Quando quiser conectar ao Supabase, substitua `NoteDatabase.insertNote()` por uma chamada à API REST do Supabase:

```kotlin
// Adicionar ao build.gradle:
// implementation 'com.squareup.okhttp3:okhttp:4.12.0'

suspend fun insertNoteRemote(content: String) {
    val client = OkHttpClient()
    val json = """{"content": "$content", "created_at": ${System.currentTimeMillis()}}"""
    val request = Request.Builder()
        .url("https://SEU_PROJETO.supabase.co/rest/v1/notes")
        .post(json.toRequestBody("application/json".toMediaType()))
        .addHeader("apikey", "SUA_ANON_KEY")
        .addHeader("Authorization", "Bearer SUA_ANON_KEY")
        .build()
    client.newCall(request).execute()
}
```

---

## Como compilar

1. Abra o Android Studio
2. **File → Open** → selecione a pasta `QuickNote/`
3. Aguarde o Gradle sync
4. **Run → Run 'app'** (ou Shift+F10)

Mínimo: Android 8.0 (API 26) | Alvo: Android 14 (API 34)
