# 文本框

## 预览

<img src="https://imgur.com/XT2iVU7.gif" alt="文本框">

## MFXTextFields

- 样式类：mfx-text-field

- 默认样式表：MFXTextField.css

- 默认皮肤：MFXTextFieldSkin.java

### 属性

| 属性         | 描述                                                                                                                                                                | 类型    |
| ------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------- | -------:|
| selectable   | 指定是否允许选择                                                                                                                                                    | Boolean |
| leadingIcon  | 指定放置在输入字段前的图标                                                                                                                                          | Node    |
| trailingIcon | 指定放置在输入字段后的图标                                                                                                                                          | Node    |
| floatingText | 指定浮动文本节点的文本                                                                                                                                              | String  |
| floating     | 指定浮动文本节点当前是否正在浮动                                                                                                                                    | Boolean |
| measureUnit  | 指定字段的度量单位。<br/>当然，这在处理表示例如：重量、体积、长度等的数字字段时很有用...                | String  |

### 可样式化属性

| 属性            | 描述                                                                                                                | CSS 属性              | 类型            | 默认值                   |
| --------------- | ------------------------------------------------------------------------------------------------------------------- | --------------------- | ---------------:| ------------------------:|
| allowEdit       | 指定字段是否可编辑                                                                                                  | -mfx-editable         | Boolean         | true                     |
| animated        | 指定浮动文本定位是否动画化                                                                                          | -mfx-animated         | Boolean         | true                     |
| borderGap       | 对于 FloatMode.BORDER FloatMode.ABOVE 模式，这指定从控件的 X 原点（不包括填充）的距离                               | -mfx-border-gap       | Double          | 10.0                     |
| caretVisible    | 指定是否应显示插入符号                                                                                              | -mfx-caret-visible    | Boolean         | true                     |
| floatMode       | 指定浮动文本浮动时的定位方式。<br/>可以是：DISABLED、ABOVE、BORDER、INLINE                                          | -mfx-float-mode       | FloatMode[枚举] | INLINE                   |
| floatingTextGap | 对于 FloatMode.INLINE 模式，这指定浮动文本节点与输入字段节点之间的间隙                                              | -mfx-gap              | Double          | 5.0                      |
| graphicTextGap  | 指定输入字段与图标之间的间隙                                                                                        | -fx-graphic-text-gap  | Double          | 10.0                     |
| measureUnitGap  | 指定字段与度量单位标签之间的间隙                                                                                    | -mfx-measure-unit-gap | Double          | 5.0                      |
| scaleOnAbove    | 指定当浮动模式设置为 FloatMode.ABOVE 时，浮动文本节点是否应该缩放                                                   | -mfx-scale-on-above   | Boolean         | false                    |
| textFill        | 指定文本颜色                                                                                                        | -fx-text-fill         | Color           | Color.rgb(0, 0, 0, 0.87) |
| textLimit       | 指定字段文本可以拥有的最大字符数                                                                                    | -mfx-text-limit       | Integer         | -1（无限制）             |

### CSS 选择器

- .mfx-text-field

- .mfx-text-field .floating-text

- .mfx-text-field .text-field（访问实际字段节点，应该不需要）

## MFXPasswordFields

- 样式类：mfx-password-field

- 默认样式表：MFXPasswordField.css

- 默认皮肤：MFXTextFieldSkin.java

### 属性

*密码字段与 MFXTextField 具有完全相同的属性*

### 可样式化属性

*除了继承自 MFXTextField 的可样式化属性外：*

| 属性          | 描述                                                             | CSS 属性            | 类型                                                                               | 默认值                                         |
| ------------- | ---------------------------------------------------------------- | ------------------- | ----------------------------------------------------------------------------------:| -----------------------------------------------:|
| allowCopy     | 指定是否允许复制密码字段文本                                     | -mfx-allow-copy     | Boolean                                                                            | false                                          |
| allowCut      | 指定是否允许从密码字段剪切文本                                   | -mfx-allow-cut      | Boolean                                                                            | false                                          |
| allowPaste    | 指定是否允许从剪贴板粘贴文本到字段                               | -mfx-allow-paste    | Boolean                                                                            | false                                          |
| showPassword  | 指定是否应取消掩码文本以显示密码                                 | -mfx-show-password  | Boolean                                                                            | false                                          |
| hideCharacter | 指定用于掩码文本的字符                                           | -mfx-hide-character | String（只能是单个字符，字符串将始终被截断为一个字符）                             | BULLET（unicode，MFXPasswordField 的公共常量） |

### CSS 选择器

*除了继承自 MFXTextField 的 CSS 选择器外：*

- .mfx-password-field .mfx-icon-wrapper（眼睛图标容器）

- .mfx-password-field .mfx-icon-wrapper .mfx-ripple-generator

- .mfx-password-field .mfx-icon-wrapper .mfx-font-icon（实际图标）
