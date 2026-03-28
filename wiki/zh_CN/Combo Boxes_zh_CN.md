# 下拉框

## 预览

<img src="https://imgur.com/BO0twpA.gif" alt="下拉框">

## MFXComboBoxes

- 样式类：mfx-combo-box

- 默认样式表：MFXComboBox.css

- 默认皮肤：MFXComboBoxSkin.java

- 默认单元格：MFXComboBoxCell.java

### 属性

| 属性              | 描述                                                                                                                                    | 类型                  |
| ----------------- | --------------------------------------------------------------------------------------------------------------------------------------- | ---------------------:|
| showing           | 指定弹出窗口是否正在显示                                                                                                                | Boolean               |
| popupAlignment    | 指定弹出窗口的位置                                                                                                                      | Alignment             |
| popupOffsetX      | 指定弹出窗口的 X 偏移量，要添加到计算出的 X 位置（来自 popupAlignment）的像素数                                                         | Double                |
| popupOffsetY      | 指定弹出窗口的 Y 偏移量，要添加到计算出的 Y 位置（来自 popupAlignment）的像素数                                                         | Double                |
| animationProvider | 指定用于打开弹出窗口的尾部图标的动画                                                                                                    | BiFunction            |
| value             | 指定下拉框的值，不一定与当前选中的项目一致                                                                                              | T[泛型]               |
| converter         | 指定用于将泛型项目转换为字符串的 StringConverter。当选中项目时用于设置下拉框文本                                                        | StringConverter       |
| items             | 指定下拉框的项目列表                                                                                                                    | ObservableList        |
| selectionModel    | 保存下拉框选择的模型                                                                                                                    | ISingleSelectionModel |
| cellFactory       | 指定用于在弹出窗口中创建项目单元格的函数                                                                                                | Function              |
| onCommit          | 指定在下拉框上按下 Enter 键时要执行的操作                                                                                               | Consumer              |
| onCancel          | 指定在下拉框上按下 Ctrl+Shift+Z 组合键时要执行的操作                                                                                    | Consumer              |

### 可样式化属性

| 属性         | 描述                                                                                     | CSS 属性            | 类型    | 默认值 |
| ------------ | ---------------------------------------------------------------------------------------- | ------------------- | -------:| ------:|
| scrollOnOpen | 指定下拉框列表是否应在打开时滚动到当前选中的值                                           | -mfx-scroll-on-open | Boolean | false  |

### CSS 选择器

- .mfx-combo-box

- .mfx-combo-box .caret（打开/关闭弹出窗口的图标）

- .mfx-combo-box .combo-popup（访问下拉框的弹出窗口）

- .mfx-combo-box .combo-popup .virtual-flow（下拉框的单元格容器）

- .mfx-combo-box .combo-popup .virtual-flow .scrollbar

- .mfx-combo-box .combo-popup .virtual-flow .mfx-combo-box-cell

- .mfx-combo-box .combo-popup .virtual-flow .data-label（单元格的文本）

  *还有其他选择器，但它们与 MFXTextField 相同，请参考其 wiki*

### 伪类

| 伪类   | 描述                                                                                                        |
| ------ | ----------------------------------------------------------------------------------------------------------- |
| :popup | 当弹出窗口显示时激活，当弹出窗口隐藏时停用。<br/>允许在弹出窗口显示时自定义下拉框 |

*还继承自 MFXTextField 的所有新伪类，因为它扩展了它*

## MFXFilterComboBoxes

- 样式类：mfx-filter-combo-box

- 默认样式表：MFXFilterComboBox.css

- 默认皮肤：MFXFilterComboBoxSkin.java

- 默认单元格：MFXFilterComboBoxCell.java（对选择正常工作很重要）

### 属性

*除了继承自 MFXComboBox 的属性外：*

| 属性               | 描述                                                                                                                                  | 类型              |
| ------------------ | ------------------------------------------------------------------------------------------------------------------------------------- | -----------------:|
| searchText         | 指定用于过滤项目列表的文本。默认情况下，此文本与弹出窗口中使用的文本字段双向绑定                                                      | String            |
| filterList         | 这是进行过滤和排序的列表。原始列表保持不变！                                                                                          | TransformableList |
| filterFunction     | 指定用于从搜索文本构建 Predicate 的函数，然后使用该谓词过滤列表                                                                       | Function          |
| resetOnPopupHidden | 指定当弹出窗口关闭时是否重置过滤器状态，例如 searchText                                                                               | Boolean           |

### 可样式化属性

*此下拉框具有与 MFXComboBox 相同的可样式化属性*

### CSS 选择器

*此下拉框具有与 MFXComboBox 相同的 CSS 选择器，唯一的区别是基本样式类为 .mfx-filter-combo-box*

### 伪类

*此下拉框具有与 MFXComboBox 相同的新伪类*
