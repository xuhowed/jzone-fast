<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>字典管理</span>
          <el-button v-auth="['sys:dict:create']" type="primary" @click="openAddType">新增字典类型</el-button>
        </div>
      </template>

      <el-row :gutter="16">
        <!-- 字典类型列表 -->
        <el-col :xs="24" :lg="10">
          <el-table :data="typeList" v-loading="typeLoading" border stripe highlight-current-row @current-change="handleTypeSelect">
            <el-table-column prop="dictName" label="字典名称" />
            <el-table-column prop="dictType" label="字典类型" />
            <el-table-column prop="status" label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '正常' : '禁用' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120">
              <template #default="{ row }">
                <el-button v-auth="['sys:dict:update']" link type="primary" @click="openEditType(row)">编辑</el-button>
                <el-button v-auth="['sys:dict:delete']" link type="danger" @click="handleDeleteType(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination v-model:current-page="typeQuery.pageIndex" :total="typeTotal" layout="prev, pager, next" style="margin-top:8px" @change="loadTypes" />
        </el-col>

        <!-- 字典项列表 -->
        <el-col :xs="24" :lg="14">
          <div class="item-header">
            <span>字典项 <el-tag v-if="currentType">{{ currentType.dictType }}</el-tag></span>
            <el-button v-auth="['sys:dict:create']" type="primary" size="small" :disabled="!currentType" @click="openAddItem">新增字典项</el-button>
          </div>
          <el-table :data="itemList" v-loading="itemLoading" border stripe>
            <el-table-column prop="itemLabel" label="标签" />
            <el-table-column prop="itemValue" label="值" />
            <el-table-column prop="sort" label="排序" width="70" />
            <el-table-column prop="status" label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '正常' : '禁用' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120">
              <template #default="{ row }">
                <el-button v-auth="['sys:dict:update']" link type="primary" @click="openEditItem(row)">编辑</el-button>
                <el-button v-auth="['sys:dict:delete']" link type="danger" @click="handleDeleteItem(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-col>
      </el-row>
    </el-card>

    <!-- 字典类型弹窗 -->
    <el-dialog v-model="typeDialogVisible" :title="typeDialogTitle" width="440px">
      <el-form ref="typeFormRef" :model="typeForm" :rules="typeRules" label-width="90px">
        <el-form-item label="字典名称" prop="dictName"><el-input v-model="typeForm.dictName" /></el-form-item>
        <el-form-item label="字典类型" prop="dictType"><el-input v-model="typeForm.dictType" :disabled="!!typeForm.id" /></el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="typeForm.status">
            <el-radio :value="1">正常</el-radio><el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注"><el-input v-model="typeForm.remark" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="typeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleTypeSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 字典项弹窗 -->
    <el-dialog v-model="itemDialogVisible" :title="itemDialogTitle" width="440px">
      <el-form ref="itemFormRef" :model="itemForm" :rules="itemRules" label-width="90px">
        <el-form-item label="字典标签" prop="itemLabel"><el-input v-model="itemForm.itemLabel" /></el-form-item>
        <el-form-item label="字典值" prop="itemValue"><el-input v-model="itemForm.itemValue" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="itemForm.sort" :min="0" /></el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="itemForm.status">
            <el-radio :value="1">正常</el-radio><el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="itemDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleItemSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
defineOptions({ name: 'SystemDict' })
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { pageDictType, addDictType, editDictType, deleteDictType, listDictItems, addDictItem, editDictItem, deleteDictItem } from '@/api/system/dict'

const typeLoading = ref(false)
const typeList = ref([])
const typeTotal = ref(0)
const typeQuery = reactive({ pageIndex: 1, pageSize: 20 })
const currentType = ref(null)

const itemLoading = ref(false)
const itemList = ref([])

const typeDialogVisible = ref(false)
const typeDialogTitle = ref('')
const typeFormRef = ref()
const typeForm = reactive({ id: null, dictName: '', dictType: '', status: 1, remark: '' })
const typeRules = {
  dictName: [{ required: true, message: '请输入字典名称', trigger: 'blur' }],
  dictType: [{ required: true, message: '请输入字典类型', trigger: 'blur' }]
}

const itemDialogVisible = ref(false)
const itemDialogTitle = ref('')
const itemFormRef = ref()
const itemForm = reactive({ id: null, dictType: '', itemLabel: '', itemValue: '', sort: 0, status: 1 })
const itemRules = {
  itemLabel: [{ required: true, message: '请输入字典标签', trigger: 'blur' }],
  itemValue: [{ required: true, message: '请输入字典值', trigger: 'blur' }]
}

async function loadTypes() {
  typeLoading.value = true
  try {
    const res = await pageDictType(typeQuery)
    typeList.value = res.records
    typeTotal.value = res.total
  } finally {
    typeLoading.value = false
  }
}

async function handleTypeSelect(row) {
  currentType.value = row
  if (!row) return
  itemLoading.value = true
  try {
    itemList.value = await listDictItems(row.dictType)
  } finally {
    itemLoading.value = false
  }
}

function openAddType() {
  typeDialogTitle.value = '新增字典类型'
  Object.assign(typeForm, { id: null, dictName: '', dictType: '', status: 1, remark: '' })
  typeDialogVisible.value = true
}

function openEditType(row) {
  typeDialogTitle.value = '编辑字典类型'
  Object.assign(typeForm, row)
  typeDialogVisible.value = true
}

async function handleTypeSubmit() {
  await typeFormRef.value.validate()
  try {
    typeForm.id ? await editDictType(typeForm) : await addDictType(typeForm)
    ElMessage.success('操作成功')
    typeDialogVisible.value = false
    loadTypes()
  } catch {}
}

async function handleDeleteType(row) {
  await ElMessageBox.confirm(`确定删除字典类型 "${row.dictName}" 吗？`, '提示', { type: 'warning' })
  try {
    await deleteDictType(row.id)
    ElMessage.success('删除成功')
    loadTypes()
  } catch {}
}

function openAddItem() {
  itemDialogTitle.value = '新增字典项'
  Object.assign(itemForm, { id: null, dictType: currentType.value.dictType, itemLabel: '', itemValue: '', sort: 0, status: 1 })
  itemDialogVisible.value = true
}

function openEditItem(row) {
  itemDialogTitle.value = '编辑字典项'
  Object.assign(itemForm, row)
  itemDialogVisible.value = true
}

async function handleItemSubmit() {
  await itemFormRef.value.validate()
  try {
    itemForm.id ? await editDictItem(itemForm) : await addDictItem(itemForm)
    ElMessage.success('操作成功')
    itemDialogVisible.value = false
    handleTypeSelect(currentType.value)
  } catch {}
}

async function handleDeleteItem(row) {
  await ElMessageBox.confirm(`确定删除字典项 "${row.itemLabel}" 吗？`, '提示', { type: 'warning' })
  try {
    await deleteDictItem(row.id)
    ElMessage.success('删除成功')
    handleTypeSelect(currentType.value)
  } catch {}
}

onMounted(loadTypes)
</script>

<style scoped lang="scss">
.card-header { display: flex; justify-content: space-between; align-items: center; }
.item-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; font-weight: 500; }
</style>
