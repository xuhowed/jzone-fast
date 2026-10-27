<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>菜单管理</span>
          <el-button v-auth="['sys:menu:create']" type="primary" @click="openAdd(null)">新增菜单</el-button>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" border row-key="id" :tree-props="{ children: 'children' }" default-expand-all>
        <el-table-column prop="menuName" label="菜单名称" min-width="160" />
        <el-table-column prop="menuType" label="类型" width="80">
          <template #default="{ row }">
            <el-tag v-if="row.menuType === 1" type="info">目录</el-tag>
            <el-tag v-else-if="row.menuType === 2" type="success">菜单</el-tag>
            <el-tag v-else type="warning">按钮</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="icon" label="图标" width="80">
          <template #default="{ row }">
            <el-icon v-if="row.icon"><component :is="row.icon" /></el-icon>
          </template>
        </el-table-column>
        <el-table-column prop="path" label="路由路径" />
        <el-table-column prop="permission" label="权限码" />
        <el-table-column prop="sort" label="排序" width="70" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '正常' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button v-auth="['sys:menu:create']" link type="primary" @click="openAdd(row)">新增子菜单</el-button>
            <el-button v-auth="['sys:menu:update']" link type="warning" @click="openEdit(row)">编辑</el-button>
            <el-button v-auth="['sys:menu:delete']" link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="560px">
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="90px">
        <el-form-item label="菜单类型" prop="menuType">
          <el-radio-group v-model="form.menuType">
            <el-radio :value="1">目录</el-radio>
            <el-radio :value="2">菜单</el-radio>
            <el-radio :value="3">按钮</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="菜单名称" prop="menuName">
          <el-input v-model="form.menuName" />
        </el-form-item>
        <el-form-item v-if="form.menuType !== 3" label="路由路径">
          <el-input v-model="form.path" />
        </el-form-item>
        <el-form-item v-if="form.menuType === 2" label="组件路径">
          <el-input v-model="form.component" placeholder="如 system/user/index" />
        </el-form-item>
        <el-form-item v-if="form.menuType === 3" label="权限码">
          <el-input v-model="form.permission" placeholder="如 sys:user:create" />
        </el-form-item>
        <el-form-item v-if="form.menuType !== 3" label="图标">
          <el-input v-model="form.icon" placeholder="Element Plus 图标名" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">正常</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="form.menuType !== 3" label="是否隐藏">
          <el-switch v-model="form.hidden" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
defineOptions({ name: 'SystemMenu' })
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMenuTree, addMenu, editMenu, deleteMenu } from '@/api/system/menu'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref()
const form = reactive({ id: null, parentId: 0, menuName: '', menuType: 1, path: '', component: '', permission: '', icon: '', sort: 0, status: 1, hidden: 0, keepAlive: 0 })
const formRules = {
  menuName: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
  menuType: [{ required: true, message: '请选择菜单类型', trigger: 'change' }]
}

async function loadData() {
  loading.value = true
  try {
    tableData.value = await getMenuTree()
  } finally {
    loading.value = false
  }
}

function openAdd(parent) {
  dialogTitle.value = '新增菜单'
  Object.assign(form, { id: null, parentId: parent?.id || 0, menuName: '', menuType: 1, path: '', component: '', permission: '', icon: '', sort: 0, status: 1, hidden: 0, keepAlive: 0 })
  dialogVisible.value = true
}

function openEdit(row) {
  dialogTitle.value = '编辑菜单'
  Object.assign(form, row)
  dialogVisible.value = true
}

async function handleSubmit() {
  await formRef.value.validate()
  submitLoading.value = true
  try {
    form.id ? await editMenu(form) : await addMenu(form)
    ElMessage.success('操作成功')
    dialogVisible.value = false
    loadData()
  } finally {
    submitLoading.value = false
  }
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确定删除菜单 "${row.menuName}" 吗？`, '提示', { type: 'warning' })
  try {
    await deleteMenu(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch {}
}

onMounted(loadData)
</script>

<style scoped lang="scss">
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
