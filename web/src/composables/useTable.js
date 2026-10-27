import { ref, reactive } from 'vue'

/**
 * 通用表格查询与分页 Composable
 * @param {Function} fetchApi 分页查询接口函数
 * @param {Object} defaultQuery 初始查询参数
 */
export function useTable(fetchApi, defaultQuery = {}) {
  const loading = ref(false)
  const tableData = ref([])
  const total = ref(0)
  const query = reactive({
    pageIndex: 1,
    pageSize: 10,
    ...defaultQuery
  })

  async function loadData() {
    loading.value = true
    try {
      const res = await fetchApi(query)
      tableData.value = res.records || []
      total.value = res.total || 0
      return res
    } finally {
      loading.value = false
    }
  }

  function resetQuery() {
    Object.keys(query).forEach(key => {
      if (key === 'pageIndex') query[key] = 1
      else if (key === 'pageSize') query[key] = defaultQuery.pageSize || 10
      else query[key] = defaultQuery[key] !== undefined ? defaultQuery[key] : null
    })
    return loadData()
  }

  return {
    loading,
    tableData,
    total,
    query,
    loadData,
    resetQuery
  }
}
