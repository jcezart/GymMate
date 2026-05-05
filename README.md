# 💪 GymMate

App Android de gerenciamento de treinos, construído com Kotlin e Jetpack Compose.

---

## 🗂️ Estrutura do projeto

```
gymmate/
├── data/          → banco Room, DAOs, entidades, mappers, implementações
├── domain/        → modelos e interfaces dos repositórios
├── di/            → módulos do Koin (injeção de dependência)
└── presentation/  → ViewModel, State, Actions e telas Compose
```

---

## 📖 Koin — Referência

### Inicializar no Application

```kotlin
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(meuModulo)
        }
    }
}
```

---

### Criar um módulo

```kotlin
val meuModulo = module {

    // Uma única instância compartilhada no app
    single<MinhaInterface> { MinhaImplementacao() }

    // Nova instância a cada vez que for pedida
    factory { MeuUseCase(repositorio = get()) }

    // ViewModel com ciclo de vida gerenciado
    viewModelOf(::MeuViewModel)
}
```

---

### `get()` — resolver dependência

O `get()` dentro de um módulo busca automaticamente uma dependência já registrada pelo tipo:

```kotlin
single<ExerciseRepository> {
    ExerciseRepositoryImpl(dao = get()) // busca o ExerciseDAO registrado
}
```

---

### Injetar o ViewModel no Compose

```kotlin
@Composable
fun MinhaRoot(
    viewModel: MeuViewModel = koinViewModel()
) { }
```

---

### `single` vs `factory`

| O que é | Use |
|---|---|
| Banco de dados, DAO | `single` |
| Repositório | `single` |
| Use Case | `factory` |
| ViewModel | `viewModelOf` |

---

## 📦 App no Play Store

<a href="https://play.google.com/store/apps/details?id=com.juliocezar.gymmate&hl=pt_BR">
  <img src="https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png" width="150">
</a>
