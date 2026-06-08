import request from "@/api/request"

export function getTaskList(params) {
  return request.get("/tasks", { params })
}

export function getTaskDetail(id) {
  return request.get(`/tasks/${id}`)
}

export function createTask(data) {
  return request.post("/tasks", data)
}

export function updateTask(id, data) {
  return request.put(`/tasks/${id}`, data)
}

export function cancelTask(id) {
  return request.delete(`/tasks/${id}`)
}

export function acceptTask(taskId) {
  return request.post(`/tasks/${taskId}/accept`)
}

export function completeTask(taskId) {
  return request.put(`/tasks/${taskId}/accept/complete`)
}

export function getAcceptInfo(taskId) {
  return request.get(`/tasks/${taskId}/accept`)
}
