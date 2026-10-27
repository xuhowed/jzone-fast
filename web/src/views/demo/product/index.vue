<template>
  <div class="product-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>商品管理</span>
          <div class="card-header-actions">
            <el-button v-auth="['demo:product:create']" type="primary" @click="openAdd">
              <el-icon><Plus /></el-icon>新增
            </el-button>
            <el-button v-auth="['demo:product:import']" type="success" @click="openImport">
              <el-icon><Upload /></el-icon>导入
            </el-button>
            <el-button v-auth="['demo:product:export']" type="warning" @click="handleExport">
              <el-icon><Download /></el-icon>导出
            </el-button>
          </div>
        </div>
      </template>

      <el-form inline :model="query" @submit.prevent="loadData">
        <el-form-item label="商品名称">
          <el-input v-model="query.productName" placeholder="商品名称" clearable />
        </el-form-item>
        <el-form-item label="商品编号">
          <el-input v-model="query.productCode" placeholder="商品编号" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="上架" :value="1" />
            <el-option label="下架" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="productCode" label="商品编号" width="130" />
        <el-table-column prop="productName" label="商品名称" min-width="150" />
        <el-table-column prop="category" label="分类" width="120" />
        <el-table-column prop="price" label="价格" width="110" align="right">
          <template #default="{ row }">
            {{ row.price != null ? `¥${row.price}` : '' }}
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="90" align="right" />
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button v-auth="['demo:product:update']" link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button v-auth="['demo:product:delete']" link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="query.pageIndex"
        v-model:page-size="query.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        style="margin-top: 16px; justify-content: flex-end"
        @change="loadData"
      />
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="520px" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="90px">
        <el-form-item label="商品编号" prop="productCode">
          <el-input v-model="form.productCode" placeholder="如 P001" />
        </el-form-item>
        <el-form-item label="商品名称" prop="productName">
          <el-input v-model="form.productName" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-input v-model="form.category" placeholder="请输入分类" />
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="form.price" :precision="2" :min="0" :step="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number v-model="form.stock" :min="0" :step="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">上架</el-radio>
            <el-radio :value="0">下架</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 导入弹窗 -->
    <el-dialog v-model="importVisible" title="导入商品" width="460px">
      <div style="margin-bottom: 12px">
        <el-button link type="primary" @click="handleDownloadTemplate">下载导入模板</el-button>
      </div>
      <el-upload
        ref="uploadRef"
        drag
        :auto-upload="false"
        :limit="1"
        accept=".xlsx,.xls"
        :on-change="handleFileChange"
        :on-exceed="handleExceed"
      >
        <el-icon style="font-size: 48px; color: #909399"><Upload /></el-icon>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <template #tip>
          <div class="el-upload__tip">仅支持 .xlsx / .xls 文件</div>
        </template>
      </el-upload>
      <template #footer>
        <el-button @click="importVisible = false">取消</el-button>
        <el-button type="primary" :loading="importLoading" :disabled="!importFile" @click="handleSubmitImport">确定导入</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
defineOptions({ name: 'DemoProduct' })
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Upload, Download } from '@element-plus/icons-vue'
import {
  pageProduct, getProductById, addProduct, editProduct, deleteProduct,
  exportProduct, importProduct, downloadTemplate
} from '@/api/demo/product'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const total = ref(0)
const query = reactive({ pageIndex: 1, pageSize: 10, productName: '', productCode: '', status: null })

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref()
const form = reactive({
  id: null, productCode: '', productName: '', category: '',
  price: 0, stock: 0, status: 1, remark: ''
})
const formRules = {
  productCode: [{ required: true, message: '请输入商品编号', trigger: 'blur' }],
  productName: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const importVisible = ref(false)
const importLoading = ref(false)
const importFile = ref(null)
const uploadRef = ref()

async function loadData() {
  loading.value = true
  try {
    const res = await pageProduct(query)
    tableData.value = res.records
    total.value = res.total
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  Object.assign(query, { pageIndex: 1, productName: '', productCode: '', status: null })
  loadData()
}

function openAdd() {
  dialogTitle.value = '新增商品'
  Object.assign(form, {
    id: null, productCode: '', productName: '', category: '',
    price: 0, stock: 0, status: 1, remark: ''
  })
  dialogVisible.value = true
}

async function openEdit(row) {
  dialogTitle.value = '编辑商品'
  try {
    const detail = await getProductById(row.id)
    Object.assign(form, detail)
    dialogVisible.value = true
  } catch {}
}

function resetForm() {
  formRef.value?.resetFields()
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    if (form.id) {
      await editProduct(form)
    } else {
      await addProduct(form)
    }
    ElMessage.success('操作成功')
    dialogVisible.value = false
    loadData()
  } finally {
    submitLoading.value = false
  }
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确定删除商品 "${row.productName}" 吗？`, '提示', { type: 'warning' })
  try {
    await deleteProduct(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch {}
}

// ============ 导入 / 导出 ============
function openImport() {
  importFile.value = null
  uploadRef.value?.clearFiles()
  importVisible.value = true
}

function handleFileChange(file) {
  importFile.value = file.raw
}

function handleExceed() {
  ElMessage.warning('只能上传一个文件，请先移除已选文件')
}

async function handleSubmitImport() {
  if (!importFile.value) return
  importLoading.value = true
  try {
    const formData = new FormData()
    formData.append('file', importFile.value)
    await importProduct(formData)
    ElMessage.success('导入成功')
    importVisible.value = false
    loadData()
  } finally {
    importLoading.value = false
  }
}

async function handleExport() {
  try {
    const response = await exportProduct(query)
    downloadBlob(response)
  } catch {}
}

async function handleDownloadTemplate() {
  try {
    const response = await downloadTemplate()
    downloadBlob(response, '商品导入模板.xlsx')
  } catch {}
}

function downloadBlob(response, defaultName = '商品数据.xlsx') {
  const disposition = response.headers?.['content-disposition']
  let filename = defaultName
  if (disposition) {
    const match = disposition.match(/filename\*?=(?:UTF-8''|")?([^";]+)/i)
    if (match) filename = decodeURIComponent(match[1].replace(/"/g, ''))
  }
  const blob = new Blob([response.data], {
    type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
  })
  const url = window.URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = filename
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  window.URL.revokeObjectURL(url)
}

onMounted(loadData)
</script>

<style scoped lang="scss">
.product-container { padding: 0; }
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  .card-header-actions {
    display: flex;
    gap: 8px;
  }
}
</style>
