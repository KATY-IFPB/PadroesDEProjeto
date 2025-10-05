# Simulador de Hidrômetros - Versão Multithread

**Contribuidor:** Pedro Cordeiro  
**Branch:** dev_Pedro.Cordeiro  
**Versão:** Multithread (até 5 simuladores concorrentes)

---

## 🚀 Como Executar o Projeto

### Requisitos
- Java 11 ou superior
- Terminal/Prompt de comando

### Compilação
```bash
cd /caminho/para/o/projeto
javac -d bin src/view/*.java src/model/*.java src/controller/*.java
```

### Execução

#### Versão Multithread 
```bash
java -cp bin controller.Orquestradora
```

#### Versão Original (Single-thread)
```bash
java -cp bin controller.HidrometroController
```

---

## 🔧 Funcionalidades da Versão Multithread

### Capacidades Expandidas
- ✅ **Até 5 simuladores simultâneos** executando independentemente
- ✅ **Comportamentos completamente isolados** para cada instância
- ✅ **Thread-safety** em todas as operações críticas
- ✅ **Configurações flexíveis** (padrão, específica por ID, ou personalizada)
- ✅ **Saídas independentes** (hidrometro_1.jpg, hidrometro_2.jpg, etc.)
- ✅ **Compatibilidade total** com o código original

### Menu de Comandos
```
0 - Criar novo simulador
1 - Modificar largura do cano de entrada
2 - Modificar largura do cano de saída  
3 - Modificar regulagem da torneira
4 - Modificar velocidade da água
5 - Ver medição do hidrômetro
6 - Listar simuladores ativos
7 - Parar simulador
9 - Sair
```

### Perfis de Configuração Pré-definidos
- **configuracao1.txt** - Residencial baixo (20mm, 45%, 0.25 m³/s)
- **configuracao2.txt** - Comercial médio (32mm, 70%, 0.55 m³/s)
- **configuracao3.txt** - Industrial alto (48mm, 88%, 0.95 m³/s)
- **configuracao4.txt** - Economia extrema (12mm, 18%, 0.08 m³/s)
- **configuracao5.txt** - Industrial máximo (55mm, 98%, 1.35 m³/s)

---

## 📋 Exemplo de Uso

### Criar Simuladores com Comportamentos Diferentes
```
1. Digite: 0 → 2 (cria simulador 1 com configuracao1.txt)
2. Digite: 0 → 2 (cria simulador 2 com configuracao2.txt)
3. Digite: 0 → 2 (cria simulador 3 com configuracao3.txt)
4. Digite: 6 (lista todos os simuladores ativos)
5. Aguarde alguns segundos e digite 6 novamente para ver as diferenças
```

### Resultado Esperado
- **Simulador 1**: Volume cresce lentamente (perfil residencial)
- **Simulador 2**: Volume cresce moderadamente (perfil comercial)
- **Simulador 3**: Volume cresce rapidamente (perfil industrial)

---

## 🏗️ Arquitetura Multithread

### Classes Principais
- **`Orquestradora.java`** - Controlador multithread principal
- **`Hidrometro.java`** - Modelo thread-safe com ID único
- **`Display.java`** - Geração de saídas independentes
- **`HidrometroController.java`** - Versão original preservada

### Recursos de Concorrência
- `ConcurrentHashMap` para armazenamento thread-safe
- `AtomicInteger` para controle de IDs únicos
- `synchronized` e `volatile` para thread-safety
- Graceful shutdown de threads individuais

---

## 📁 Estrutura de Arquivos

```
src/
├── controller/
│   ├── HidrometroController.java  (versão original)
│   └── Orquestradora.java         (versão multithread)
├── model/
│   └── Hidrometro.java            (thread-safe)
├── view/
│   ├── Display.java               (saídas independentes)
│   └── Messages.java
├── configuracao.txt               (configuração padrão)
├── configuracao1.txt              (perfil residencial)
├── configuracao2.txt              (perfil comercial)
├── configuracao3.txt              (perfil industrial)
├── configuracao4.txt              (perfil economia)
└── configuracao5.txt              (perfil máximo)

saida/
├── leitura_do_hidrometro.jpg      (simulador original)
├── leitura_do_hidrometro_1.jpg    (simulador 1)
├── leitura_do_hidrometro_2.jpg    (simulador 2)
├── leitura_do_hidrometro_3.jpg    (simulador 3)
├── leitura_do_hidrometro_4.jpg    (simulador 4)
└── leitura_do_hidrometro_5.jpg    (simulador 5)
```

---

## 🔄 Diferenças da Versão Original

### Funcionalidades Adicionadas
- Suporte para múltiplos simuladores concorrentes
- Menu expandido com controles avançados
- Sistema de configurações flexível
- Arquivos de saída independentes por simulador
- Thread-safety completa

### Compatibilidade
- ✅ Código original 100% preservado e funcional
- ✅ Mesmas dependências e requisitos
- ✅ Interface de linha de comando mantida
- ✅ Sem quebras de funcionalidade

---

## 📊 Validação e Testes

Durante os testes, simuladores com configurações diferentes apresentaram volumes drasticamente distintos (diferença de até 43x), comprovando a independência total dos comportamentos.

---

## 🎥 Vídeos Demonstrativos (Versão Original)

Para entender o funcionamento base do simulador:
- https://www.youtube.com/watch?v=T1ETk-Jvqvs
- https://www.youtube.com/watch?v=Kerw61viips

---

## 📐 Diagrama de Classes

O diagrama de classes foi criado em: https://www.planttext.com/  
Para mais informações, consulte o arquivo "CodigoDiagramaDEClasses"

---

## 🤝 Contribuição

Esta implementação multithread foi desenvolvida como evolução do projeto original, mantendo total compatibilidade e adicionando capacidades empresariais de simulação simultânea.

**Repositório:** https://github.com/KATY-IFPB/PadroesDEProjeto  
**Branch Original:** main  
**Branch Multithread:** dev_Pedro.Cordeiro

**Repositório:** https://github.com/KATY-IFPB/PadroesDEProjeto  
**Branch Original:** main  
**Branch Multithread:** dev_Pedro.Cordeiro
