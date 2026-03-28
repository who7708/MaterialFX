# 单选按钮

## 预览

<img src="https://imgur.com/ArUhH58.gif" alt="复选框">

## MFXRadioButtons

- 样式类：mfx-radio-button

- 默认样式表：MFXRadioButton.css

- 默认皮肤：MFXRadioButtonSkin.java

### 可样式化属性

| 属性               | 描述                                                                                                                                                                                                         | CSS 属性             | 类型                 | 默认值              |
| ------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | -------------------- | --------------------:| -------------------:|
| contentDisposition | 指定控件相对于其文本的位置                                                                                                                                                                                   | -mfx-content-disposition | ContentDisplay[枚举] | ContentDisplay.LEFT |
| gap                | 指定控件与其文本之间的间距                                                                                                                                                                                   | -mfx-gap             | Double               | 8.0                 |
| radioGap           | 指定单选按钮外圆和内圆之间的间隙                                                                                                                                                                             | -mfx-radio-gap       | Double               | 3.5                 |
| radius             | 指定单选按钮的半径                                                                                                                                                                                           | -mfx-radius          | Double               | 8.0                 |
| textExpand         | 当为控件设置特定大小（例如使用 setPrefSize，这也适用于 SceneBuilder）时，此标志将告诉控件的标签占用所有可用空间。这允许结合 contentDisposition 以多种有趣的方式布局控件内容。当文本展开（此属性为 true）时，使用 alignment 属性来定位文本。 | -mfx-text-expand     | Boolean              | false               |

### CSS 选择器

- .mfx-radio-button

- .mfx-radio-button .label（单选按钮的文本，不应该需要，因为绑定到单选按钮）

- .mfx-radio-button .mfx-ripple-generator

- .mfx-radio-button .radio

- .mfx-radio-button .dot
