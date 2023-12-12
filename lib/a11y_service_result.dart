import 'package:a11y_service/a11y_service_event.dart';
import 'package:a11y_service/a11y_service_node.dart';

class A11yServiceResult {
  A11yServiceEvent? event;
  Map<String, A11yServiceNode>? nodes;

  A11yServiceResult({
    this.event,
    this.nodes,
  });

  A11yServiceResult copyWith({
    A11yServiceEvent? event,
    Map<String, A11yServiceNode>? nodes,
  }) {
    return A11yServiceResult(
      event: event ?? this.event,
      nodes: nodes ?? this.nodes,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'event': event,
      'nodes': nodes,
    };
  }

  factory A11yServiceResult.fromJson(Map<String, dynamic> json) {
    return A11yServiceResult(
      event: json['event'] == null ? null : A11yServiceEvent.fromJson(Map.from(json['event'])),
      nodes: (json['nodes'] as Map<dynamic, dynamic>?)?.map((k, e) => MapEntry(k!, A11yServiceNode.fromJson(Map.from(e)))),
    );
  }

  @override
  String toString() {
    String str = '\n---↓↓↓ RESULT ↓↓↓---\nEVENT:\n$event\nNODES:\n';
    nodes?.forEach((treeId, e) {
      str += '$treeId : $e\n';
    });
    str += '\n---↑↑↑ RESULT ↑↑↑---\n';
    return str;
  }

  @override
  int get hashCode => Object.hash(event, nodes);

  @override
  bool operator ==(Object other) => identical(this, other) || other is A11yServiceResult && runtimeType == other.runtimeType && event == other.event && nodes == other.nodes;
}
