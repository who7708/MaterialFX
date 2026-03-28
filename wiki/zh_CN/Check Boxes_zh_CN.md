# 复选框

## 预览

<img src="https://imgur.com/ArUhH58.gif" alt="复选框">

## MFXCheckBoxes

- 样式类：mfx-checkbox

- 默认样式表：MFXCheckBox.css

- 默认皮肤：MFXCheckboxSkin.java

### 可样式化属性

| 属性               | 描述                                                                                                                                                                                                         | CSS 属性                 | 类型                 | 默认值              |
| ------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | ------------------------ | --------------------:| -------------------:|
| contentDisposition | 指定控件相对于其文本的位置                                                                                                                                                                                   | -mfx-content-disposition | ContentDisplay[枚举] | ContentDisplay.LEFT |
| gap                | 指定控件与其文本之间的间距                                                                                                                                                                                   | -mfx-gap                 | Double               | 8.0                 |
| textExpand         | 当为控件设置特定大小（例如使用 setPrefSize，这也适用于 SceneBuilder）时，此标志将告诉控件的标签占用所有可用空间。这允许结合 contentDisposition 以多种有趣的方式布局控件内容。当文本展开（此属性为 true）时，使用 alignment 属性来定位文本。 | -mfx-text-expand         | Boolean              | false               |

### CSS 选择器

- .mfx-checkbox

- .mfx-checkbox .label（不是必需的，因为绑定到复选框）

- .mfx-checkbox .ripple-container（包含波纹生成器和框）

- .mfx-checkbox .ripple-container .mfx-ripple-generator

- .mfx-checkbox .ripple-container .box（标记容器）

- .mfx-checkbox .ripple-container .box .mark（勾选标记图标）

  *注意，您应该能够在不使用 .ripple-container 选择器的情况下访问框和标记*
