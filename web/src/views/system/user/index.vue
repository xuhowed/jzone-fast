<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
          <el-button v-auth="['sys:user:create']" type="primary" @click="openAdd">新增用户</el-button>
        </div>
      </template>

      <el-form inline :model="query" @submit.prevent="loadData">
        <el-form-item label="用户名">
          <el-input v-model="query.username" placeholder="用户名" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width:100px">
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="nickname" label="昵称" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="phone" label="手机号" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '正常' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="300" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.username !== 'admin'" v-auth="['sys:user:update']" link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button v-if="row.username !== 'admin'" v-auth="['sys:user:update']" link type="primary" @click="openAssignRole(row)">分配角色</el-button>
            <el-button v-auth="['sys:user:update']" link type="warning" @click="openResetPwd(row)">重置密码</el-button>
            <el-button v-if="row.username !== 'admin'" v-auth="['sys:user:delete']" link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="query.pageIndex"
        v-model:page-size="query.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        style="margin-top:16px;justify-content:flex-end"
        @change="loadData"
      />
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="80px">
        <el-form-item v-if="!form.id" label="用户名" prop="username">
          <el-input v-model="form.username" />
        </el-form-item>
        <el-form-item v-if="!form.id" label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">正常</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 重置密码弹窗 -->
    <el-dialog v-model="resetPwdVisible" title="重置密码" width="400px">
      <el-form ref="pwdFormRef" :model="pwdForm" :rules="pwdRules" label-width="80px">
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="pwdForm.newPassword" type="password" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="resetPwdVisible = false">取消</el-button>
        <el-button type="primary" @click="handleResetPwd">确定</el-button>
      </template>
    </el-dialog>

    <!-- 分配角色弹窗 -->
    <el-dialog v-model="assignRoleVisible" title="分配角色" width="500px">
      <div style="margin-bottom: 12px; color: #606266;">
        用户：<strong>{{ assignRoleUser.username }}</strong>
      </div>
      <el-checkbox-group v-model="assignRoleForm.roleIds">
        <el-checkbox
          v-for="role in allRoles"
          :key="role.id"
          :label="role.id"
          :value="role.id"
          style="display: block; margin-bottom: 8px;"
        >
          {{ role.roleName }}
          <span style="color: #909399; font-size: 12px;">（{{ role.roleCode }}）</span>
        </el-checkbox>
      </el-checkbox-group>
      <template #footer>
        <el-button @click="assignRoleVisible = false">取消</el-button>
        <el-button type="primary" :loading="assignRoleLoading" @click="handleAssignRole">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
defineOptions({ name: 'SystemUser' })
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { pageUser, getUserById, addUser, editUser, deleteUser, resetPassword, assignRole } from '@/api/system/user'
import { listRole } from '@/api/system/role'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const total = ref(0)
const query = reactive({ pageIndex: 1, pageSize: 10, username: '', status: null })

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref()
const form = reactive({ id: null, username: '', password: '', nickname: '', email: '', phone: '', status: 1 })
const formRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const resetPwdVisible = ref(false)
const pwdFormRef = ref()
const pwdForm = reactive({ id: null, newPassword: '' })
const pwdRules = { newPassword: [{ required: true, min: 6, message: '密码至少6位', trigger: 'blur' }] }

async function loadData() {
  loading.value = true
  try {
    const res = await pageUser(query)
    tableData.value = res.records
    total.value = res.total
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  Object.assign(query, { pageIndex: 1, username: '', status: null })
  loadData()
}

function openAdd() {
  dialogTitle.value = '新增用户'
  Object.assign(form, { id: null, username: '', password: '', nickname: '', email: '', phone: '', status: 1 })
  dialogVisible.value = true
}

function openEdit(row) {
  dialogTitle.value = '编辑用户'
  Object.assign(form, { id: row.id, username: row.username, password: '', nickname: row.nickname, email: row.email, phone: row.phone, status: row.status })
  dialogVisible.value = true
}

function resetForm() {
  formRef.value?.resetFields()
}

async function handleSubmit() {
  await formRef.value.validate()
  submitLoading.value = true
  try {
    if (form.id) {
      await editUser({ id: form.id, nickname: form.nickname, email: form.email, phone: form.phone, status: form.status })
    } else {
      await addUser(form)
    }
    ElMessage.success('操作成功')
    dialogVisible.value = false
    loadData()
  } finally {
    submitLoading.value = false
  }
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确定删除用户 "${row.username}" 吗？`, '提示', { type: 'warning' })
  try {
    await deleteUser(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch {}
}

function openResetPwd(row) {
  pwdForm.id = row.id
  pwdForm.newPassword = ''
  resetPwdVisible.value = true
}

async function handleResetPwd() {
  await pwdFormRef.value.validate()
  try {
    await resetPassword(pwdForm)
    ElMessage.success('密码重置成功')
    resetPwdVisible.value = false
  } catch {}
}

// 分配角色
const assignRoleVisible = ref(false)
const assignRoleLoading = ref(false)
const assignRoleUser = reactive({ id: null, username: '' })
const assignRoleForm = reactive({ userId: null, roleIds: [] })
const allRoles = ref([])

async function openAssignRole(row) {
  assignRoleUser.id = row.id
  assignRoleUser.username = row.username
  try {
    const [userDetail, roles] = await Promise.all([
      getUserById(row.id),
      listRole()
    ])
    allRoles.value = roles
    assignRoleForm.userId = row.id
    assignRoleForm.roleIds = userDetail.roleIds || []
    assignRoleVisible.value = true
  } catch {}
}

async function handleAssignRole() {
  assignRoleLoading.value = true
  try {
    await assignRole(assignRoleForm)
    ElMessage.success('分配成功')
    assignRoleVisible.value = false
    loadData()
  } finally {
    assignRoleLoading.value = false
  }
}

onMounted(loadData)
</script>

<style scoped lang="scss">
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
