# 表格视图

## 预览

<img src="https://imgur.com/nj6xhUT.gif" alt="表格视图">

## MFXTableViews

- 样式类：mfx-table-view

- 默认样式表：MFXTableView.css

- 默认皮肤：MFXTableViewSkin.java

- 默认单元格：MFXTableRow.java

- 默认列：MFXTableColumn.java

### 属性

| 属性                   | 描述                                                                                                                                                                                                         | 类型                     |
| ---------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | ------------------------:|
| virtualFlowInitialized | 用于通知表格布局已初始化/准备就绪的有用属性。<br/>例如，它被 autosizeColumnsOnInitialization() 方法用于在使用监听器布局表格之前自动调整列大小。<br/>一旦 SimpleVirtualFlow 检索到单元格的高度，即视为已初始化。 | Boolean                  |
| items                  | 指定包含项目的表格 ObservableList。                                                                                                                                                                          | ObservableList           |
| selectionModel         | 保存表格选择的模型                                                                                                                                                                                           | IMultipleSelectionModel  |
| tableColumns           | 包含表格列的列表                                                                                                                                                                                             | ObservableList           |
| tableRowFactory        | 指定用于生成表格行的函数                                                                                                                                                                                     | Function                 |
| transformableList      | 这是进行过滤和排序的列表。原始列表保持不变！                                                                                                                                                                 | TransformableListWrapper |
| filters                | 包含 MFXFilterPane 用于过滤表格的过滤器信息的列表                                                                                                                                                            | ObservableList           |
| footerVisible          | 指定表格页脚是否可见                                                                                                                                                                                         | Boolean                  |

### CSS 选择器

- .mfx-table-view

- .mfx-table-view .columns-container

- .mfx-table-view .columns-container .mfx-table-column

- .mfx-table-view .virtual-flow

- .mfx-table-view .virtual-flow .scrollbar

- .mfx-table-view .virtual-flow .mfx-table-row

- .mfx-table-view .default-footer

- .mfx-table-view .default-footer .mfx-icon-wrapper

- .mfx-table-view .default-footer .mfx-icon-wrapper .mfx-ripple-generator

- .mfx-table-view .default-footer .mfx-icon-wrapper .mfx-font-icon

## MFXPaginatedTableView

- 样式类：mfx-paginated-table-view

- 默认样式表：MFXTableView.css

- 默认皮肤：MFXPaginatedTableViewSkin.java

- 默认单元格：MFXTableRow.java

- 默认列：MFXTableColumn.java

### 属性

*除了继承自 MFXTableView 的属性外：*

| 属性        | 描述                                                               | 类型    |
| ----------- | ------------------------------------------------------------------ | -------:|
| currentPage | 指定当前显示的页面                                                 | Integer |
| maxPage     | 指定最后一页的索引                                                 | Integer |
| pagesToShow | 指定分页控件一次可以显示多少页                                     | Integer |
| rowsPerPage | 指定表格每页可以显示多少行                                         | Integer |

### CSS 选择器

*除了 MFXTableView 的 CSS 选择器外：*

- .mfx-paginated-table-view .default-footer .mfx-pagination（访问分页控件）

<br/><br/>

## MFXTableViews 使用的单元格/子组件

## MFXTableRow

- 样式类：mfx-table-row

- 默认样式表：MFXTableView.css

### 属性

| 属性     | 描述                                        | 类型           |
| -------- | ------------------------------------------- | --------------:|
| cells    | 行的单元格作为不可修改的可观察列表          | ObservableList |
| index    | 指定行在 SimpleVirtualFlow 中的索引         | Integer        |
| data     | 指定行代表的项目                            | T[泛型]        |
| selected | 指定行的选择状态                            | Boolean        |

### CSS 选择器

- .mfx-table-row

- .mfx-table-row .mfx-ripple-generator

- .mfx-table-row .mfx-table-row-cell

## MFXTableRowCell

- 样式类：mfx-table-row-cell

- 默认样式表：MFXTableView.css

- 默认皮肤：MFXTableRowCellSkin.java

### 属性

| 属性            | 描述                                                                                                                                                                                                         | 类型            |
| --------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | ---------------:|
| extractor       | 单元格用于从泛型表格项目 T 中提取单元格数据 E 的函数。<br/>示例：表格包含城市列表，第二列代表城市的人口，该函数从 City 对象中提取人口字段 | Function        |
| converter       | 用于将提取的 E 字段转换为字符串的 StringConverter，该字符串将成为单元格的文本                                                                | StringConverter |
| leadingGraphic  | 指定单元格的前导节点                                                                                                                                                                                         | Node            |
| trailingGraphic | 指定单元格的尾部节点                                                                                                                                                                                         | Node            |

### CSS 选择器

- .mfx-table-row-cell

- .mfx-table-row-cell .label（不是必需的，因为它绑定到行单元格）

## MFXTableColumn

- 样式类：mfx-table-column

- 默认样式表：MFXTableView.css

- 默认皮肤：MFXTableColumnSkin.java

### 属性

| 属性           | 描述                                                                    | 类型            |
| -------------- | ----------------------------------------------------------------------- | ---------------:|
| rowCellFactory | 指定用于构建行单元格的函数                                              | Function        |
| sortState      | 指定列的排序状态，可以是 UNSORTED、ASCENDING、DESCENDING                | SortState[枚举] |
| comparator     | 指定用于对列进行排序的 Comparator}                                      | Comparator      |
| dragged        | 指定是否正在拖动列                                                      | Boolean         |
| columnResozable | 指定是否可以调整列的大小                                               | Boolean         |

### CSS 选择器

- .mfx-table-column

- .mfx-table-column .laber（不是必需的，因为它绑定到表格列）

- .mfx-table-column .mfx-icon-wrapper（排序图标容器）

- .mfx-table-column .mfx-icon-wrapper .mfx-ripple-generator

- .mfx-table-column .mfx-icon-wrapper .mfx-font-icon（实际的排序图标）
