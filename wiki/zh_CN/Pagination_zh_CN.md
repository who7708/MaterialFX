# 分页

## 预览

<img src="https://imgur.com/nj6xhUT.gif" alt="表格视图">

## MFXPaginations

- 样式类：mfx-pagination

- 默认样式表：MFXPagination.css

- 默认皮肤：MFXPaginationSkin.java

- 默认单元格：MFXPage.java

### 属性

| 属性                       | 描述                                                                                            | 类型              |
| -------------------------- | ----------------------------------------------------------------------------------------------- | -----------------:|
| currentPage                | 指定当前选中的页面                                                                              | Integer           |
| maxPage                    | 指定最大页面数                                                                                  | Integer           |
| pagesToShow                | 指定一次显示的最大页面数                                                                        | Integer           |
| indexesSupplier            | 此供应商指定用于构建页面的算法                                                                  | Supplier          |
| pageCellFactory            | 此函数指定如何将索引转换为页面                                                                  | Function          |
| ellipseString              | 指定用于截断页面的字符串                                                                        | String            |
| orientation                | 指定控件的方向                                                                                  | Orientation[枚举] |
| showPopupForTruncatedPages | 指定截断的页面是否应在点击时显示包含中间页面的弹出窗口                                          | Boolean           |

### CSS 选择器

- .mfx-pagination

- .mfx-pagination .mfx-icon-wrapper（左/右箭头容器）

- .mfx-pagination .mfx-icon-wrapper .mfx-ripple-generator

- .mfx-pagination .mfx-icon-wrapper .mfx-font-icon（实际的左/右图标）

- .mfx-pagination .pages-bar（页面容器）

- .mfx-pagination .pages-bar .mfx-page

<br/>

<br/>

## MFXPages

- 样式类：mfx-page

- 默认样式表：MFXPagination.css

### 属性

| 属性     | 描述                                                   | 类型        |
| -------- | ------------------------------------------------------ | -----------:|
| index    | 指定页面的索引                                         | Integer     |
| between  | 隐藏页面的范围，如果截断则为 null                      | NumberRange |
| selected | 指定页面的选择状态                                     | Boolean     |

### CSS 选择器

- .mfx-page

- .mfx-page .pages-popup

- .mfx-page .pages-popup .virtual-flow

- .mfx-page .pages-popup .mfx-list-view

- .mfx-page .pages-popup .virtual-flow .mfx-list-cell
