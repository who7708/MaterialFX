# 列表

## 预览

<img src="https://imgur.com/4Ckdn5z.gif" alt="列表视图">

## MFXListViews

- 样式类：mfx-list-view

- 默认样式表：MFXListView.css

- 默认皮肤：MFXListViewSkin.java

- 默认单元格：MFXListCell.java

### 属性

| 属性            | 描述                                                                                             | 类型                    |
| --------------- | ------------------------------------------------------------------------------------------------ | -----------------------:|
| items           | 项目列表属性                                                                                     | ObservableList          |
| converter       | 指定用于将泛型项目转换为字符串的 StringConverter，由列表单元格使用                               | StringConverter         |
| selectionModel  | 保存列表选择的模型                                                                               | IMultipleSelectionModel |
| cellFactory     | 指定用于构建列表单元格的函数                                                                     | Function                |
| trackColor      | 指定滚动条轨道的颜色                                                                             | Paint                   |
| thumbColor      | 指定滚动条滑块的颜色                                                                             | Paint                   |
| thumbHoverColor | 指定鼠标悬停时滚动条滑块的颜色                                                                   | Paint                   |
| hideAfter       | 指定滚动条隐藏前的时间                                                                           | Duration                |

### 可样式化属性

| 属性           | 描述                                                                           | CSS 属性             | 类型             | 默认值              |
| -------------- | ------------------------------------------------------------------------------ | -------------------- | ----------------:| -------------------:|
| hideScrollBars | 指定当鼠标不在列表上时是否隐藏滚动条                                           | -mfx-hide-scrollbars | Boolean          | false               |
| depthLevel     | 指定控件周围阴影的强度                                                         | -mfx-depth-level     | DepthLevel[枚举] | DepthLevel.LEVEL2   |

### CSS 选择器

- .mfx-list-view

- .mfx-list-view .virtual-flow

- .mfx-list-view .virtual-flow .scroll-bar

- .mfx-list-view .virtual-flow .mfx-list-cell

- .mfx-list-view .virtual-flow .mfx-list-cell .mfx-ripple-generator

- .mfx-list-view .virtual-flow .mfx-list-cell .data-label（单元格的文本）

## MFXCheckListViews

- 样式类：mfx-check-list-view

- 默认样式表：MFXCheckListView.css

- 默认皮肤：MFXListViewSkin.java

- 默认单元格：MFXCheckListCell.java

### 属性和可样式化属性

*此列表视图与 MFXListView 具有完全相同的属性和可样式化属性。*

### CSS 选择器

- .mfx-check-list-view

- .mfx-check-list-view .virtual-flow

- .mfx-check-list-view .virtual-flow .scroll-bar

- .mfx-check-list-view .virtual-flow .mfx-check-list-cell

- .mfx-check-list-view .virtual-flow .mfx-check-list-cell .mfx-ripple-generator

- .mfx-check-list-view .virtual-flow .mfx-check-list-cell .data-label（单元格的文本）

- .mfx-check-list-view .virtual-flow .mfx-check-list-cell .mfx-checkbox
