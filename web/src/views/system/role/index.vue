<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>角色管理</span>
          <el-button v-auth="['sys:role:create']" type="primary" @click="openAdd">新增角色</el-button>
        </div>
      </template>

      <el-form inline :model="query" @submit.prevent="loadData">
        <el-form-item label="角色名称">
          <el-input v-model="query.roleName" placeholder="角色名称" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="roleName" label="角色名称" />
        <el-table-column prop="roleCode" label="角色编码" />
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '正常' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <template v-if="row.roleCode !== 'admin'">
              <el-button v-auth="['sys:role:update']" link type="primary" @click="openEdit(row)">编辑</el-button>
              <el-button v-auth="['sys:role:update']" link type="warning" @click="openAssignMenu(row)">分配菜单</el-button>
              <el-button v-auth="['sys:role:delete']" link type="danger" @click="handleDelete(row)">删除</el-button>
            </template>
            <span v-else style="color: #909399; font-size: 13px;">系统内置角色</span>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="query.pageIndex"
        v-model:page-size="query.pageSize"
        :total="total"
        layout="total, sizes, prev, pager, next"
        style="margin-top:16px;justify-content:flex-end"
        @change="loadData"
      />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="480px">
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="90px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" />
        </el-form-item>
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="form.roleCode" :disabled="!!form.id" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">正常</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="menuDialogVisible" title="分配菜单" width="400px">
      <el-tree
        ref="menuTreeRef"
        :data="menuTreeData"
        :props="{ label: 'menuName', children: 'children' }"
        node-key="id"
        show-checkbox
        default-expand-all
      />
      <template #footer>
        <el-button @click="menuDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAssignMenu">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
defineOptions({ name: 'SystemRole' })
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { pageRole, addRole, editRole, deleteRole, assignMenu, getRoleMenuIds } from '@/api/system/role'
import { getMenuTree } from '@/api/system/menu'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const total = ref(0)
const query = reactive({ pageIndex: 1, pageSize: 10, roleName: '' })

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref()
const form = reactive({ id: null, roleName: '', roleCode: '', sort: 0, status: 1, remark: '' })
const formRules = {
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  roleCode: [{ required: true, message: '请输入角色编码', trigger: 'blur' }]
}

const menuDialogVisible = ref(false)
const menuTreeRef = ref()
const menuTreeData = ref([])
const currentRoleId = ref(null)

async function loadData() {
  loading.value = true
  try {
    const res = await pageRole(query)
    tableData.value = res.records
    total.value = res.total
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  Object.assign(query, { pageIndex: 1, roleName: '' })
  loadData()
}

function openAdd() {
  dialogTitle.value = '新增角色'
  Object.assign(form, { id: null, roleName: '', roleCode: '', sort: 0, status: 1, remark: '' })
  dialogVisible.value = true
}

function openEdit(row) {
  dialogTitle.value = '编辑角色'
  Object.assign(form, row)
  dialogVisible.value = true
}

async function handleSubmit() {
  await formRef.value.validate()
  submitLoading.value = true
  try {
    form.id ? await editRole(form) : await addRole(form)
    ElMessage.success('操作成功')
    dialogVisible.value = false
    loadData()
  } finally {
    submitLoading.value = false
  }
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确定删除角色 "${row.roleName}" 吗？`, '提示', { type: 'warning' })
  try {
    await deleteRole(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch {}
}

function filterLeafKeys(treeData, keys) {
  const leafKeys = []
  const keySet = new Set(keys)
  function traverse(nodes) {
    for (const node of nodes) {
      if (!node.children || node.children.length === 0) {
        if (keySet.has(node.id)) {
          leafKeys.push(node.id)
        }
      } else {
        traverse(node.children)
      }
    }
  }
  traverse(treeData)
  return leafKeys
}

async function openAssignMenu(row) {
  currentRoleId.value = row.id
  const [tree, checkedIds] = await Promise.all([getMenuTree(), getRoleMenuIds(row.id)])
  menuTreeData.value = tree
  menuDialogVisible.value = true
  await nextTick()
  const leafKeys = filterLeafKeys(tree, checkedIds)
  menuTreeRef.value?.setCheckedKeys(leafKeys, false)
}

async function handleAssignMenu() {
  const checkedKeys = [
    ...menuTreeRef.value.getCheckedKeys(),
    ...menuTreeRef.value.getHalfCheckedKeys()
  ]
  try {
    await assignMenu(currentRoleId.value, checkedKeys)
    ElMessage.success('分配成功')
    menuDialogVisible.value = false
  } catch {}
}

onMounted(loadData)
</script>

<style scoped lang="scss">
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
