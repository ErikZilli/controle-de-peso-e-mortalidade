# Controle de Peso e Mortalidade

App simples pra acompanhar peso por idade e mortalidade de lotes de frango em aviários. Um código só (`web/`), dois jeitos de rodar:

- **PWA (iPhone e Android):** `web/` publicado no GitHub Pages. Abrir o link e "Adicionar à tela inicial".
- **APK Android:** `android/` embute `web/` numa WebView. Build: `cd android` e `gradlew.bat assembleDebug` (JDK 17 + Android SDK).

Dados: `localStorage` (chave `controle-alojamento.v1`), só no aparelho, sem servidor. PDF: jsPDF (local em `web/`); compartilha pelo Android (`AndroidBridge.sharePdf`) ou pela Web Share API no iPhone.

Peso recomendado: 7 dias = 4,3 x peso do pinto; 14d = 2,5x, 21d = 2x, 28d = 1,7x, 35d = 1,5x o peso da idade anterior (real se já pesado, senão o recomendado).
