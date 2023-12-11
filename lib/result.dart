class A11yServiceResult {
  A11yServiceEvent? event;
  List<Node>? nodes;

  A11yServiceResult({
    this.event,
    this.nodes,
  });

  A11yServiceResult copyWith({
    A11yServiceEvent? event,
    List<Node>? nodes,
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
      nodes: (json['nodes'] as List<dynamic>?)?.map((e) => Node.fromJson(Map.from(e))).toList(),
    );
  }

  @override
  String toString() {
    String str = '\n---↓↓↓ RESULT ↓↓↓---\nEVENT:\n$event\nNODES:\n';
    nodes?.forEach((e) {
      str += '$e\n';
    });
    str += '\n---↑↑↑ RESULT ↑↑↑---\n';
    return str;
  }

  @override
  int get hashCode => Object.hash(event, nodes);

  @override
  bool operator ==(Object other) => identical(this, other) || other is A11yServiceResult && runtimeType == other.runtimeType && event == other.event && nodes == other.nodes;
}

class A11yServiceEvent {
  String? packageName;
  String? className;
  String? text;
  String? description;

  A11yServiceEvent({
    this.packageName,
    this.className,
    this.text,
    this.description,
  });

  A11yServiceEvent copyWith({
    String? packageName,
    String? className,
    String? text,
    String? description,
  }) {
    return A11yServiceEvent(
      packageName: packageName ?? this.packageName,
      className: className ?? this.className,
      text: text ?? this.text,
      description: description ?? this.description,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'packageName': packageName,
      'className': className,
      'text': text,
      'description': description,
    };
  }

  factory A11yServiceEvent.fromJson(Map<String, dynamic> json) {
    return A11yServiceEvent(
      packageName: json['packageName'] as String?,
      className: json['className'] as String?,
      text: json['text'] as String?,
      description: json['description'] as String?,
    );
  }

  @override
  String toString() => '$packageName / $className { $text | $description }';

  @override
  int get hashCode => Object.hash(packageName, className, text, description);

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is A11yServiceEvent && runtimeType == other.runtimeType && packageName == other.packageName && className == other.className && text == other.text && description == other.description;
}

class Node {
  List<int>? depth;
  List<int>? bounds;
  String? id;
  String? packageName;
  String? className;
  String? text;
  String? description;
  bool? clickable;
  bool? scrollable;
  bool? editable;

  Node({
    this.depth,
    this.bounds,
    this.id,
    this.packageName,
    this.className,
    this.text,
    this.description,
    this.clickable,
    this.scrollable,
    this.editable,
  });

  Node copyWith({
    List<int>? depth,
    List<int>? bounds,
    String? id,
    String? packageName,
    String? className,
    String? text,
    String? description,
    bool? clickable,
    bool? scrollable,
    bool? editable,
  }) {
    return Node(
      depth: depth ?? this.depth,
      bounds: bounds ?? this.bounds,
      id: id ?? this.id,
      packageName: packageName ?? this.packageName,
      className: className ?? this.className,
      text: text ?? this.text,
      description: description ?? this.description,
      clickable: clickable ?? this.clickable,
      scrollable: scrollable ?? this.scrollable,
      editable: editable ?? this.editable,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'depth': depth,
      'bounds': bounds,
      'id': id,
      'packageName': packageName,
      'className': className,
      'text': text,
      'description': description,
      'clickable': clickable,
      'scrollable': scrollable,
      'editable': editable,
    };
  }

  factory Node.fromJson(Map<String, dynamic> json) {
    return Node(
      depth: (json['depth'] as List<dynamic>?)?.map((e) => e as int).toList(),
      bounds: (json['bounds'] as List<dynamic>?)?.map((e) => e as int).toList(),
      id: json['id'] as String?,
      packageName: json['packageName'] as String?,
      className: json['className'] as String?,
      text: json['text'] as String?,
      description: json['description'] as String?,
      clickable: json['clickable'] as bool?,
      scrollable: json['scrollable'] as bool?,
      editable: json['editable'] as bool?,
    );
  }

  @override
  String toString() => '$depth : $packageName / $className { $id | $text | $description | $bounds | $clickable | $scrollable | $editable }';

  @override
  int get hashCode => Object.hash(depth, bounds, id, packageName, className, text, description, clickable, scrollable, editable);

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is Node &&
          runtimeType == other.runtimeType &&
          depth == other.depth &&
          bounds == other.bounds &&
          id == other.id &&
          packageName == other.packageName &&
          className == other.className &&
          text == other.text &&
          description == other.description &&
          clickable == other.clickable &&
          scrollable == other.scrollable &&
          editable == other.editable;
}
