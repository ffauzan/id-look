# In-Game GM Handbook Plugin

## Preview

![](https://s3.oxy.my.id/public/files/idlook110.gif)

## 1. Installation
1. Download IdLook-1.1.0.jar from Release.
2. Put it to your Grasscutter plugin folder.
> It is advisable to remove the `IdLookPlugin` folder if you are updating from an older version.

## 2. Config File Explanations
```
{
  "scoreTreshold": 40,      // Treshold for similarity search. Range 1-100.
  "resultLimit": 3,         // How many search result should be displayed.
  "defaultLanguage": "EN"   // Default language for the plugin.
}
```

## 3. Command Usage
### Searching for items, avatars, or monsters.
```
 /gm {your search query}
 /l {your search query}
 ```
### Changing Handbook language
```
/gm setlang {language code}
/l setlang {language code}
```

## 4. Available Languages
```
EN,CHS,CHT,JP,KR,DE,ES,FR,ID,PT,RU,TH,VI
```

## 5. Version Compatibility

| IdLook | Grasscutter Stable | Grasscutter Development |
|--------|--------------------|--------------------|
| 1.1.0  |                    | 1.3.2-dev          |
| 1.0.3  |                    | 1.2.3-dev          |
| 1.0.2  |                    | 1.2.2-dev [(949916a and newer)](https://github.com/Grasscutters/Grasscutter/commit/949916ad8060afbd31507b4c5f62427fb6dd59bb)         |
| 1.0.1  | 1.2.0              | 1.2.2-dev          |
