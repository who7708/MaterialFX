# 开关

## 预览

<img src="https://imgur.com/ArUhH58.gif" alt="复选框">

## MFXToggleButtons

- 样式类：mfx-toggle-button

- 默认样式表：MFXToggleButton.css

- 默认皮肤：MFXToggleButtonSkin.java

### 属性

| 属性        | 描述                                                              | 类型         |
| ----------- | ----------------------------------------------------------------- | ------------:|
| toggleGroup | 指定此开关所属的 ToggleGroup                                      | ToggleGroup  |
| selected    | 指定开关的选择状态                                                | Boolean      |
| onAction    | 指定开关选择状态改变时要执行的操作                                | EventHandler |

### 可样式化属性

| 属性               | 描述                                                                                                                                                                                                         | CSS 属性             | 类型           | 默认值              |
| ------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | -------------------- | --------------:| -------------------:|
| contentDisposition | 指定控件相对于其文本的位置                                                                                                                                                                                   | -mfx-content-disposition | ContentDisplay | ContentDisplay.LEFT |
| gap                | 指定控件与其文本之间的间距                                                                                                                                                                                   | -mfx-gap             | Double         | 8.0                 |
| length             | 指定开关按钮线的长度                                                                                                                                                                                         | -mfx-length          | Double         | 36.0                |
| radius             | 指定开关按钮圆的半径                                                                                                                                                                                         | -mfx-radius          | Double         | 10.0                |
| textExpand         | 当为控件设置特定大小（例如使用 setPrefSize，这也适用于 SceneBuilder）时，此标志将告诉控件的标签占用所有可用空间。这允许结合 contentDisposition 以多种有趣的方式布局控件内容。当文本展开（此属性为 true）时，使用 alignment 属性来定位文本。 | -mfx-text-expand     | Boolean        | false               |

### CSS 选择器

- .mfx-toggle-button

- .mfx-toggle-button .label（不是必需的，因为绑定到开关）

- .mfx-toggle-button .mfx-ripple-generator

- .mfx-toggle-button .line（开关的线）

- .mfx-toggle-button .circle（开关的圆）

### 伪类

| 伪类        | 描述                                   |
| ----------- | -------------------------------------- |
| ":selected" | 允许在选中开关时自定义开关             |

## MFXCircleToggleNodes

- 样式类：mfx-circle-toggle-node

- 默认样式表：MFXCircleToggleNode.css

- 默认皮肤：MFXCircleToggleNodeSkin.java

### 属性

| 属性              | 描述                         | 类型 |
| ----------------- | ---------------------------- | ----:|
| labelLeadingIcon  | 指定标签的前导图标           | Node |
| labelTrailingIcon | 指定标签的尾部图标           | Node |

### 可样式化属性

| 属性         | 描述                                   | CSS 属性           | 类型               | 默认值              |
| ------------ | -------------------------------------- | ------------------ | ------------------:| -------------------:|
| gap          | 指定开关与其文本之间的间隙             | -mfx-gap           | Double             | 5.0                 |
| size         | 指定开关的半径                         | -mfx-size          | Double             | 32.0                |
| textPosition | 指定标签的位置，在开关上方或下方       | -mfx-text-position | TextPosition[枚举] | TextPosition.BOTTOM |

### CSS 选择器

- .mfx-circle-toggle-node

- .mfx-circle-toggle-node .mfx-ripple-generator

- .mfx-circle-toggle-node .circle

- .mfx-circle-toggle-node .mfx-text-field（开关的文本，充当标签）

## MFXRectangleToggleNodes

- 样式类：mfx-rectangle-toggle-node

- 默认样式表：MFXRectangleToggleNode.css

- 默认皮肤：MFXRectangleToggleNodeSkin.java

### 属性

*此开关具有与 MFXCircleToggleNode 相同的属性，但没有可样式化属性*

### CSS 选择器

- .mfx-rectangle-toggle-node

- .mfx-rectangle-toggle-node .mfx-ripple-generator

- 

- .mfx-rectangle-toggle-node .mfx-text-field（开关的文本，充当标签）
