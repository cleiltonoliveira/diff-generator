# JSON Diff Processor

Utilitário para **comparação estrutural de JSON** e **objetos Java**, com foco em:

- auditoria
- validação de compatibilidade entre versões
- persistência de mudanças em banco de dados

O diff é **semântico**, baseado na estrutura do JSON.

---

## ✨ Características

- Diff estrutural (não textual)
- Independente da ordem dos campos
- Funciona com JSON String ou objetos Java
- Geração de paths legíveis (`customer.address.zipCode`)
- Detecção precisa de:
    - ADD
    - REMOVE
    - CHANGE
- Arrays simples comparados **sem considerar ordem**
- Compatível com tipos Java Time

---

## 📦 Estrutura principal

```text
com.example.multimodule
├── service
│   └── JsonDiffProcessor
├── model
│   ├── JsonChange
│   └── ChangeType
```

🚀 Uso
A partir de JSON String
```
List<JsonChange> changes =
    JsonDiffProcessor.diffAsJsonFromString(
        oldJson,
        newJson,
        false
    );
```
A partir de objetos Java
```
List<JsonChange> changes =
    JsonDiffProcessor.diffAsJsonFromObject(
        oldObject,
        newObject,
        false
    );
```

🧾 Estrutura do resultado

Cada mudança é representada por JsonChange:
```
public record JsonChange(
    String path,
    ChangeType type,
    JsonNode oldValue,
    JsonNode newValue
) {}
```

Exemplo
```
{
    "path": "customer.address.country",
    "type": "ADD",
    "oldValue": null,
    "newValue": "US"
}
```

🧠 Regras de comparação
Objetos JSON
- Ordem dos campos não importa 
- Campos são comparados por nome
- Mudança de tipo gera CHANGE

Arrays  
**Arrays simples (ordem ignorada)**

Arrays contendo apenas valores simples (`string`, `number`, `boolean`, `null`)
são comparados sem considerar ordem.
```
["ADMIN", "USER"]
["USER", "ADMIN"]
```

**Arrays complexos (ordem considerada)**

Arrays contendo objetos ou arrays internos são comparados por índice.
```
items[0].sku
items[1].price
```
