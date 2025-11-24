# Guia de Criação de Templates - Moments App

Este guia explica como criar novos templates de vídeo para o app Moments.

## 📋 Índice

1. [Visão Geral](#visão-geral)
2. [Estrutura de um Template](#estrutura-de-um-template)
3. [Passo a Passo](#passo-a-passo)
4. [Exemplo Completo](#exemplo-completo)
5. [Boas Práticas](#boas-práticas)
6. [Limitações Técnicas](#limitações-técnicas)

---

## Visão Geral

Um **template** no Moments define como imagens e vídeos selecionados pelo usuário serão combinados em um vídeo final. Cada template especifica:

- Quantos "moments" (segmentos) o vídeo terá
- A duração de cada moment
- A categoria do template
- A thumbnail de preview

---

## Estrutura de um Template

### Modelo de Dados

```kotlin
data class Template(
    val id: Int,                        // ID único do template
    val title: String,                  // Nome exibido ao usuário
    val thumbnailResId: Int,            // Recurso drawable da thumbnail
    val momentsCount: Int,              // Número de moments (mídias)
    val durationSeconds: Int,           // Duração total em segundos
    val category: TemplateCategory,     // Categoria do template
    val momentDurations: List<Float>    // Duração de cada moment em segundos
)

enum class TemplateCategory {
    FEATURE,        // Templates em destaque
    NEW,            // Templates novos
    MOST_VIEWED     // Templates mais vistos
}
```

### Regras Importantes

✅ **OBRIGATÓRIO:**
- `momentsCount` deve ser igual ao tamanho da lista `momentDurations`
- A soma de `momentDurations` deve ser aproximadamente igual a `durationSeconds`
- Cada duração em `momentDurations` deve ser > 0

⚠️ **RECOMENDAÇÕES:**
- Duração mínima por moment: **1.0 segundo**
- Duração máxima por moment: **5.0 segundos**
- Duração total máxima do template: **40 segundos**
- Número de moments: entre **3 e 10**

---

## Passo a Passo

### 1. Adicionar a Thumbnail

Adicione a imagem de preview do template em:
```
app/src/main/res/drawable/
```

**Especificações da thumbnail:**
- Formato: PNG ou JPG
- Proporção recomendada: 9:16 (vertical)
- Resolução sugerida: 720x1280px
- Nome: `template_X.png` (onde X é o número do template)

### 2. Definir as Durações dos Moments

Planeje quanto tempo cada imagem/vídeo ficará na tela:

```kotlin
// Exemplo: Template com 7 moments e 15.2s de duração total
val momentDurations = listOf(
    1.6f,  // Moment 1: 1.6 segundos
    2.4f,  // Moment 2: 2.4 segundos
    2.8f,  // Moment 3: 2.8 segundos
    1.8f,  // Moment 4: 1.8 segundos
    1.2f,  // Moment 5: 1.2 segundos
    3.2f,  // Moment 6: 3.2 segundos
    2.2f   // Moment 7: 2.2 segundos
)
// Soma: 15.2 segundos
```

### 3. Calcular a Duração Total

```kotlin
val durationSeconds = momentDurations.sum().toInt() // 15.2 -> 15
```

### 4. Criar o Template Object

Adicione o template na lista correspondente em `HomeScreen.kt`:

```kotlin
val featureTemplates = listOf(
    Template(
        id = 1,
        title = "Feature 1",
        thumbnailResId = R.drawable.template_1,
        momentsCount = 7,
        durationSeconds = 15,
        category = TemplateCategory.FEATURE,
        momentDurations = listOf(1.6f, 2.4f, 2.8f, 1.8f, 1.2f, 3.2f, 2.2f)
    )
)
```

---

## Exemplo Completo

Aqui está o **Template "Feature 1"** implementado:

### Características
- **Nome**: Feature 1
- **Moments**: 7
- **Duração Total**: 15 segundos (15.2s real)
- **Categoria**: FEATURE

### Implementação

```kotlin
// app/src/main/java/com/example/moments/ui/screens/HomeScreen.kt

val featureTemplates = listOf(
    Template(
        id = 1,
        title = "Feature 1",
        thumbnailResId = R.drawable.template_1,
        momentsCount = 7,
        durationSeconds = 15,
        category = TemplateCategory.FEATURE,
        momentDurations = listOf(
            1.6f,  // Moment 1
            2.4f,  // Moment 2
            2.8f,  // Moment 3
            1.8f,  // Moment 4
            1.2f,  // Moment 5
            3.2f,  // Moment 6
            2.2f   // Moment 7
        )
    )
)
```

### Como o Template Funciona

1. **Usuário seleciona template** na HomeScreen
2. **Escolhe 7 imagens/vídeos** na galeria (ChooseMediasScreen)
3. **VideoComposer processa**:
   - 1ª imagem/vídeo → 1.6s
   - 2ª imagem/vídeo → 2.4s
   - 3ª imagem/vídeo → 2.8s
   - 4ª imagem/vídeo → 1.8s
   - 5ª imagem/vídeo → 1.2s
   - 6ª imagem/vídeo → 3.2s
   - 7ª imagem/vídeo → 2.2s
4. **Vídeo final**: 15.2 segundos, salvo na galeria
5. **Preview**: Reproduz com controles

---

## Boas Práticas

### ✅ Faça

- **Varie as durações**: Crie ritmo alternando durações curtas e longas
- **Teste no dispositivo**: Sempre teste o template em um dispositivo real
- **Use thumbnails atraentes**: A thumbnail é o que atrai o usuário
- **Nomeie descritivamente**: Use nomes que indiquem o estilo do template

**Exemplo de ritmo bom:**
```kotlin
momentDurations = listOf(
    2.0f,  // Lenta - introdução
    1.5f,  // Rápida
    3.0f,  // Lenta - destaque
    1.2f,  // Rápida
    2.5f   // Média - finalização
)
```

### ❌ Evite

- **Durações muito curtas**: < 0.5s (dificil de visualizar)
- **Durações muito longas**: > 5.0s (pode entediar)
- **Todas durações iguais**: Perde ritmo e dinamismo
- **Muitos moments**: > 10 (vídeo muito longo)
- **Poucos moments**: < 3 (vídeo muito curto)

---

## Limitações Técnicas

### Media3 Transformer

O app usa **Media3 Transformer 1.5.0** para composição de vídeo. Requisitos:

#### Para Imagens:
```kotlin
EditedMediaItem.Builder(mediaItem)
    .setFrameRate(30)        // OBRIGATÓRIO! Sem isso: IllegalStateException
    .setRemoveAudio(true)
    .build()
```

#### Para Vídeos:
```kotlin
MediaItem.Builder()
    .setUri(uri)
    .setClippingConfiguration(
        MediaItem.ClippingConfiguration.Builder()
            .setStartPositionMs(0)
            .setEndPositionMs(durationMs)
            .build()
    )
    .build()
```

#### Ao Misturar Imagens e Vídeos:
```kotlin
Composition.Builder(sequences)
    .experimentalSetForceAudioTrack(true)  // OBRIGATÓRIO!
    .build()
```

### Limites do Sistema

| Parâmetro | Limite |
|-----------|--------|
| Duração máxima total | 60 segundos
| Moments mínimos | 1 |
| Moments máximos | 15 (recomendado: 10) |
| Frame rate (imagens) | 30 fps (fixo) |
| Codec de vídeo | H.264 (video/avc) |
| Resolução de saída | Baseada na mídia original |

---

## Criando Diferentes Categorias

### Template FEATURE (Destaque)

```kotlin
val featureTemplates = listOf(
    Template(
        id = 1,
        title = "Smooth Transitions",
        thumbnailResId = R.drawable.template_smooth,
        momentsCount = 5,
        durationSeconds = 12,
        category = TemplateCategory.FEATURE,
        momentDurations = listOf(2.5f, 2.0f, 3.0f, 2.5f, 2.0f)
    )
)
```

### Template NEW (Novo)

```kotlin
val newTemplates = listOf(
    Template(
        id = 10,
        title = "Quick Cuts",
        thumbnailResId = R.drawable.template_quickcuts,
        momentsCount = 8,
        durationSeconds = 10,
        category = TemplateCategory.NEW,
        momentDurations = listOf(1.5f, 1.0f, 1.5f, 1.0f, 1.5f, 1.0f, 1.5f, 1.0f)
    )
)
```

### Template MOST_VIEWED (Mais Visto)

```kotlin
val mostViewedTemplates = listOf(
    Template(
        id = 20,
        title = "Classic Story",
        thumbnailResId = R.drawable.template_classic,
        momentsCount = 6,
        durationSeconds = 18,
        category = TemplateCategory.MOST_VIEWED,
        momentDurations = listOf(3.0f, 3.0f, 3.0f, 3.0f, 3.0f, 3.0f)
    )
)
```

---

## Checklist de Validação

Antes de adicionar um novo template, verifique:

- [ ] ID único não conflita com outros templates
- [ ] Thumbnail adicionada em `drawable/`
- [ ] `momentsCount` = tamanho de `momentDurations`
- [ ] Soma de `momentDurations` ≈ `durationSeconds`
- [ ] Todas durações > 0
- [ ] Duração total ≤ 40 segundos
- [ ] Template testado em dispositivo real
- [ ] Vídeo gerado sem erros
- [ ] Preview funciona corretamente

---

## Arquivos Modificados

Ao adicionar um novo template, você modificará:

1. **`app/src/main/res/drawable/`**
   - Adicionar thumbnail (ex: `template_X.png`)

2. **`app/src/main/java/com/example/moments/ui/screens/HomeScreen.kt`**
   - Adicionar template na lista apropriada:
     - `featureTemplates`
     - `newTemplates`
     - `mostViewedTemplates`

---

## Referência Rápida

### Template Mínimo Válido

```kotlin
Template(
    id = 99,
    title = "Minimal",
    thumbnailResId = R.drawable.template_99,
    momentsCount = 3,
    durationSeconds = 6,
    category = TemplateCategory.NEW,
    momentDurations = listOf(2.0f, 2.0f, 2.0f)
)
```

### Template Recomendado

```kotlin
Template(
    id = 100,
    title = "Recommended",
    thumbnailResId = R.drawable.template_100,
    momentsCount = 7,
    durationSeconds = 15,
    category = TemplateCategory.FEATURE,
    momentDurations = listOf(2.0f, 2.5f, 2.0f, 2.0f, 2.5f, 2.0f, 2.0f)
)
```

---

## Suporte

Para dúvidas sobre criação de templates:

1. Consulte `VIDEO_COMPOSITION_GUIDE.md` para detalhes técnicos de composição
2. Veja implementações existentes em `HomeScreen.kt`
3. Teste sempre em dispositivo real antes de finalizar

---

**Última atualização**: 2025-01-23
**Versão do app**: 1.0
**Media3 Transformer**: 1.5.0
