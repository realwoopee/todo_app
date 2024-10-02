/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { TodoItem } from '../models/TodoItem';
import type { TodoItemModel } from '../models/TodoItemModel';
import type { TodoItemPatchModel } from '../models/TodoItemPatchModel';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class TodoItemService {
    /**
     * @returns TodoItem Success
     * @throws ApiError
     */
    public static getTasks(): CancelablePromise<Array<TodoItem>> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/tasks',
        });
    }
    /**
     * @param requestBody
     * @returns TodoItem Success
     * @throws ApiError
     */
    public static addTask(
        requestBody?: TodoItemModel,
    ): CancelablePromise<TodoItem> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/tasks',
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * @param requestBody
     * @returns TodoItem Success
     * @throws ApiError
     */
    public static loadTasks(
        requestBody?: Array<TodoItem>,
    ): CancelablePromise<Array<TodoItem>> {
        return __request(OpenAPI, {
            method: 'PUT',
            url: '/tasks',
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * @param id
     * @returns TodoItem Success
     * @throws ApiError
     */
    public static getTask(
        id: string,
    ): CancelablePromise<TodoItem> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/tasks/{id}',
            path: {
                'id': id,
            },
        });
    }
    /**
     * @param id
     * @param requestBody
     * @returns TodoItem Success
     * @throws ApiError
     */
    public static editTask(
        id: string,
        requestBody?: TodoItemPatchModel,
    ): CancelablePromise<TodoItem> {
        return __request(OpenAPI, {
            method: 'PATCH',
            url: '/tasks/{id}',
            path: {
                'id': id,
            },
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * @param id
     * @returns any Success
     * @throws ApiError
     */
    public static deleteTask(
        id: string,
    ): CancelablePromise<any> {
        return __request(OpenAPI, {
            method: 'DELETE',
            url: '/tasks/{id}',
            path: {
                'id': id,
            },
        });
    }
}
