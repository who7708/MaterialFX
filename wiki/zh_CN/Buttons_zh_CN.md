# 按钮

## 预览

<img src="https://imgur.com/jATdGFL.gif" alt="按钮">

## MFXButtons

- 样式类：mfx-button

- 默认样式表：MFXButton.css

- 默认皮肤：MFXButtonSkin.java

### 属性

| 属性                    | 描述                                           | 类型    |
| ----------------------- | ---------------------------------------------- | -------:|
| computeRadiusMultiplier | 指定 rippleRadiusMultiplier 属性是否应由波纹生成器自动计算 | Boolean |
| rippleAnimateBackground | 指定是否应同时动画化按钮的背景                 | Boolean |
| rippleAnimateShadow     | 指定是否应同时动画化按钮的阴影                 | Boolean |
| rippleAnimationSpeed    | 指定波纹生成器的动画速度                       | Double  |
| rippleBackgroundOpacity | 指定背景动画的不透明度（如果 rippleAnimateBackground 为 true） | Double  |
| rippleColor             | 指定波纹颜色                                   | Color   |
| rippleRadius            | 指定波纹半径                                   | Double  |
| rippleRadiusMultiplier  | 指定波纹半径将乘以的数字                       | Double  |

### 可样式化属性

| 属性       | 描述                                                                         | CSS 属性         | 类型             | 默认值              |
| ---------- | ---------------------------------------------------------------------------- | ---------------- | ----------------:| -------------------:|
| depthLevel | 指定应用于按钮的 DropShadow 效果的强度，使其呈现 3D 效果                     | -mfx-depth-level | DepthLevel[枚举] | DepthLevel.LEVEL2   |
| buttonType | 指定按钮的外观。可以是 FLAT 或 RAISED。在第一种模式下，depthLevel 被忽略     | -mfx-button-type | ButtonType[枚举] | ButtonType.FLAT     |

### CSS 选择器

- .mfx-button

- .mfx-button .mfx-ripple-generator
