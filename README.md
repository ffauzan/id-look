# In-Game GM Handbook Plugin
[PREVIEW](https://s3.oxy.my.id/public/files/idlook.gif)

## Installation
1. Download IdLook-1.0.1.jar from Release.
2. Put it to your Grasscutter plugin folder.

## Config
```
{
  "scoreTreshold": 40,  // Treshold for similarity search. Range 1-100.
  "resultLimit": 3      // How many search result should be displayed.
}
```

## Command Usage
1. > /gm {your search query}
2. > /l {your search query}

### Version Compatibility
The plugin template is currently on Grasscutter version `1.2.0` and `1.2.2-dev`.

| IdLook | Grasscutter Stable | Grasscutter Development |
|--------|--------------------|--------------------|
| 1.0.2  |                    | 1.2.2-dev [(949916a and newer)](https://github.com/Grasscutters/Grasscutter/commit/949916ad8060afbd31507b4c5f62427fb6dd59bb)         |
| 1.0.1  | 1.2.0              | 1.2.2-dev          |